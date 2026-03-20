package com.ntt.ms_movimientos.application.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.ntt.ms_movimientos.adapters.in.web.dto.MovimientoResponse;
import com.ntt.ms_movimientos.adapters.in.web.dto.ReporteEstadoCuentaResponse;
import com.ntt.ms_movimientos.adapters.in.web.dto.ReporteEstadoCuentaResponse.ClienteReporteItem;
import com.ntt.ms_movimientos.adapters.in.web.dto.ReporteEstadoCuentaResponse.CuentaReporteItem;
import com.ntt.ms_movimientos.adapters.in.web.dto.ReporteEstadoCuentaResponse.MovimientoReporteItem;
import com.ntt.ms_movimientos.application.port.in.MovimientoUseCase;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class ReporteEstadoCuentaService {

	private final MovimientoUseCase movimientoUseCase;
	private final RestTemplate restTemplate;

	@Value("${app.services.cuentas.base-url:http://localhost:8082}")
	private String cuentasBaseUrl;

	@Value("${app.services.clientes.base-url:http://localhost:8081}")
	private String clientesBaseUrl;

	public ReporteEstadoCuentaService(MovimientoUseCase movimientoUseCase) {
		this.movimientoUseCase = movimientoUseCase;
		this.restTemplate = new RestTemplate();
	}

	@Transactional(readOnly = true)
	public ReporteEstadoCuentaResponse generar(UUID clienteId, Instant from, Instant to) {
		ClienteReporteItem cliente = fetchClienteSafe(clienteId);
		List<CuentaDto> cuentas = fetchCuentas();

		// Si no se envía rango, devolvemos todos los movimientos (y el PDF muestra el rango vacío).
		// Reglas:
		// - ambos null => sin filtro
		// - uno null => rango abierto (from=Instant.EPOCH / to=Instant.now())
		Instant effectiveFrom = from != null ? from : Instant.EPOCH;
		Instant effectiveTo = to != null ? to : Instant.now();

		List<CuentaDto> cuentasCliente = cuentas.stream()
				.filter(c -> clienteId.equals(c.clienteId))
				.sorted(Comparator.comparing(c -> c.numeroCuenta))
				.toList();

		List<CuentaReporteItem> cuentaItems = cuentasCliente.stream().map(c -> {
			List<MovimientoResponse> movimientos = movimientoUseCase.listByCuentaAndDateRange(c.id, effectiveFrom, effectiveTo);

			List<MovimientoReporteItem> movItems = movimientos.stream()
					.sorted(Comparator.comparing(MovimientoResponse::fecha))
					.map(m -> new MovimientoReporteItem(
							m.id(),
							m.cuentaId(),
							c.numeroCuenta,
							c.tipo,
							m.fecha(),
							m.tipo() != null ? m.tipo().name() : null,
							m.valor(),
							m.saldoDisponible(),
							m.estado() != null ? m.estado().name() : null,
							m.sagaId(),
							m.commandId(),
							m.createdAt()
					))
					.toList();

			// Para totales del estado de cuenta solo consideramos movimientos aplicados.
			BigDecimal totalCreditos = movimientos.stream()
					.filter(m -> m.estado() != null && "APPLIED".equalsIgnoreCase(m.estado().name()))
					.filter(m -> m.tipo() != null && "CREDITO".equalsIgnoreCase(m.tipo().name()))
					.map(MovimientoResponse::valor)
					.filter(v -> v != null)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal totalDebitos = movimientos.stream()
					.filter(m -> m.estado() != null && "APPLIED".equalsIgnoreCase(m.estado().name()))
					.filter(m -> m.tipo() != null && "DEBITO".equalsIgnoreCase(m.tipo().name()))
					.map(MovimientoResponse::valor)
					.filter(v -> v != null)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			return new CuentaReporteItem(
					c.id,
					c.numeroCuenta,
					c.tipo,
					c.saldoInicial,
					c.saldoActual,
					movItems,
					totalCreditos,
					totalDebitos
			);
		}).toList();

		BigDecimal totalCreditos = cuentaItems.stream()
				.map(CuentaReporteItem::totalCreditos)
				.filter(v -> v != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalDebitos = cuentaItems.stream()
				.map(CuentaReporteItem::totalDebitos)
				.filter(v -> v != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		String pdfBase64 = Base64.getEncoder()
				.encodeToString(renderPdfBytes(cliente, clienteId, from, to, cuentaItems, totalCreditos, totalDebitos));

		return new ReporteEstadoCuentaResponse(clienteId, cliente, from, to, cuentaItems, totalCreditos, totalDebitos, pdfBase64);
	}

	private List<CuentaDto> fetchCuentas() {
		String url = cuentasBaseUrl + "/cuentas";
		ResponseEntity<ApiResponseCuentaList> resp = restTemplate.getForEntity(url, ApiResponseCuentaList.class);
		ApiResponseCuentaList body = resp.getBody();
		if (body == null || body.data == null) {
			return List.of();
		}
		return body.data;
	}

	private byte[] renderPdfBytes(
			ClienteReporteItem cliente,
			UUID clienteId,
			Instant from,
			Instant to,
			List<CuentaReporteItem> cuentas,
			BigDecimal totalCreditos,
			BigDecimal totalDebitos
	) {
		String html = buildHtml(cliente, clienteId, from, to, cuentas, totalCreditos, totalDebitos);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.withHtmlContent(html, null);
			builder.toStream(baos);
			builder.run();
			return baos.toByteArray();
		} catch (Exception e) {
			throw new IllegalStateException("Error generando PDF", e);
		}
	}

	private String buildHtml(
			ClienteReporteItem cliente,
			UUID clienteId,
			Instant from,
			Instant to,
			List<CuentaReporteItem> cuentas,
			BigDecimal totalCreditos,
			BigDecimal totalDebitos
	) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><meta charset=\"UTF-8\"/>");
		sb.append("<style>");
		// Layout base
		sb.append("@page { size: A4; margin: 24px 26px; }");
		sb.append("body { font-family: Arial, sans-serif; font-size: 11px; color: #111827; }");
		sb.append("h1 { margin: 0; font-size: 18px; }");
		sb.append("h2 { margin: 14px 0 6px 0; font-size: 14px; }");
		sb.append("hr { border: 0; border-top: 1px solid #E5E7EB; margin: 12px 0; }");

		// Header / Cards
		sb.append(".header { display: block; padding-bottom: 10px; border-bottom: 2px solid #111827; }");
		sb.append(".muted { color: #6B7280; }");
		sb.append(".row { margin-top: 6px; }");
		sb.append(".pill { display: inline-block; padding: 2px 8px; border-radius: 999px; background: #F3F4F6; }");
		sb.append(".kpi { margin-top: 8px; padding: 10px; background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 8px; }");
		sb.append(".kpi b { font-size: 12px; }");
		sb.append(".pos { color: #065F46; }"); // verde
		sb.append(".neg { color: #991B1B; }"); // rojo

		// Table
		sb.append("table { width: 100%; border-collapse: collapse; margin-top: 8px; }");
		sb.append("th, td { border: 1px solid #E5E7EB; padding: 6px; }");
		sb.append("th { background: #111827; color: white; text-align: left; }");
		sb.append("td.num { text-align: right; font-variant-numeric: tabular-nums; }");
		sb.append("tr:nth-child(even) td { background: #F9FAFB; }");
		sb.append(".badge { display:inline-block; padding: 1px 6px; border-radius: 6px; background:#EEF2FF; }");

		// Footer
		sb.append(".footer { margin-top: 18px; font-size: 10px; color: #6B7280; }");
		sb.append("</style>");
		sb.append("</head><body>");

		// Header
		sb.append("<div class=\"header\">");
		sb.append("<h1>Estado de cuenta</h1>");
		sb.append("<div class=\"row muted\">Generado: ").append(fmt.format(Instant.now())).append("</div>");
		sb.append("<div class=\"row\"><span class=\"pill\"><b>ClienteId:</b> ").append(escape(clienteId != null ? clienteId.toString() : "")).append("</span></div>");

		if (cliente != null) {
			sb.append("<div class=\"row\"><span class=\"pill\"><b>Cliente:</b> ").append(escape(cliente.nombre())).append("</span></div>");
			sb.append("<div class=\"row muted\"><b>Identificación:</b> ").append(escape(cliente.identificacion()))
					.append(" &#160;&#160; <b>Teléfono:</b> ").append(escape(cliente.telefono()))
					.append(" &#160;&#160; <b>Dirección:</b> ").append(escape(cliente.direccion()))
					.append("</div>");
		} else {
			sb.append("<div class=\"row muted\"><b>Cliente:</b> No encontrado en ms-clientes</div>");
		}
		sb.append("<div class=\"row muted\"><b>Rango:</b> ").append(from != null ? fmt.format(from) : "")
				.append(" a ").append(to != null ? fmt.format(to) : "").append("</div>");
		sb.append("</div>");

		// KPI global
		sb.append("<div class=\"kpi\">");
		sb.append("<div><b>Total créditos:</b> <span class=\"pos\">").append(totalCreditos != null ? totalCreditos.toPlainString() : "0").append("</span>")
				.append(" &#160;&#160; <b>Total débitos:</b> <span class=\"neg\">").append(totalDebitos != null ? totalDebitos.toPlainString() : "0").append("</span>")
				.append("</div>");
		sb.append("<div class=\"muted\">Cuentas en el reporte: ").append(cuentas != null ? cuentas.size() : 0).append("</div>");
		sb.append("</div>");

		sb.append("<hr/>");

		for (CuentaReporteItem c : cuentas) {
			int totalMovs = c.movimientos() != null ? c.movimientos().size() : 0;
			// Saldo final: último saldoDisponible de un movimiento APPLIED (por fecha).
			// Si no hay movimientos aplicados en el rango, hacemos fallback a saldoActual.
			BigDecimal saldoFinal = null;
			if (c.movimientos() != null) {
				saldoFinal = c.movimientos().stream()
						.filter(m -> m.estado() != null && "APPLIED".equalsIgnoreCase(m.estado()))
						.sorted(Comparator.comparing(MovimientoReporteItem::fecha))
						.map(MovimientoReporteItem::saldoDisponible)
						.filter(v -> v != null)
						.reduce((first, second) -> second)
						.orElse(null);
			}
			if (saldoFinal == null) {
				saldoFinal = c.saldoActual();
			}

			sb.append("<h2>Cuenta ").append(escape(c.numeroCuenta())).append(" <span class=\"badge\">").append(escape(c.tipoCuenta())).append("</span></h2>");
			sb.append("<div class=\"muted\">CuentaId: ").append(escape(c.cuentaId() != null ? c.cuentaId().toString() : "")).append("</div>");

			sb.append("<div class=\"kpi\">");
			sb.append("<div><b>Saldo inicial:</b> ").append(c.saldoInicial() != null ? c.saldoInicial().toPlainString() : "")
					.append(" &#160;&#160; <b>Saldo actual:</b> ").append(c.saldoActual() != null ? c.saldoActual().toPlainString() : "")
					.append(" &#160;&#160; <b>Saldo final (según movimientos):</b> ").append(saldoFinal != null ? saldoFinal.toPlainString() : "-")
					.append("</div>");
			sb.append("<div><b>Créditos:</b> <span class=\"pos\">").append(c.totalCreditos() != null ? c.totalCreditos().toPlainString() : "0").append("</span>")
					.append(" &#160;&#160; <b>Débitos:</b> <span class=\"neg\">").append(c.totalDebitos() != null ? c.totalDebitos().toPlainString() : "0").append("</span>")
					.append(" &#160;&#160; <b># Movimientos:</b> ").append(totalMovs)
					.append("</div>");
			sb.append("</div>");

			sb.append("<table><thead><tr>");
			sb.append("<th style=\"width:22%\">Fecha</th><th style=\"width:12%\">Tipo</th><th style=\"width:14%\">Valor</th><th style=\"width:18%\">Saldo disp.</th><th style=\"width:14%\">Estado</th>");
			sb.append("</tr></thead><tbody>");

			if (c.movimientos() != null && !c.movimientos().isEmpty()) {
				for (MovimientoReporteItem m : c.movimientos()) {
					boolean isDebito = m.tipoMovimiento() != null && "DEBITO".equalsIgnoreCase(m.tipoMovimiento());
					String cls = isDebito ? "neg" : "pos";

					sb.append("<tr>");
					sb.append("<td>").append(m.fecha() != null ? fmt.format(m.fecha()) : "").append("</td>");
					sb.append("<td>").append(escape(m.tipoMovimiento())).append("</td>");
					sb.append("<td class=\"num ").append(cls).append("\">")
							.append(m.valor() != null ? m.valor().toPlainString() : "")
							.append("</td>");
					sb.append("<td class=\"num\">").append(m.saldoDisponible() != null ? m.saldoDisponible().toPlainString() : "-").append("</td>");
					sb.append("<td>").append(escape(m.estado())).append("</td>");
					sb.append("</tr>");
				}
			} else {
				sb.append("<tr><td colspan=\"5\" class=\"muted\">Sin movimientos en el rango seleccionado.</td></tr>");
			}

			sb.append("</tbody></table>");
			sb.append("<hr/>");
		}

		sb.append("<div class=\"footer\">Documento generado por ms-movimientos. Fuente de cuentas: ms-cuentas.</div>");
		sb.append("</body></html>");
		return sb.toString();
	}

	private String escape(String s) {
		if (s == null) return "";
		StringBuilder out = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
				case '&' -> out.append("&");
				case '<' -> out.append("<");
				case '>' -> out.append(">");
				case '"' -> out.append('"');
				case '\'' -> out.append("&#39;");
				default -> out.append(ch);
			}
		}
		return out.toString();
	}

	@SuppressWarnings("unused")
	static class ApiResponseCuentaList {
		public boolean success;
		public int status;
		public String message;
		public String correlationId;
		public List<CuentaDto> data;
	}

	@SuppressWarnings("unused")
	static class ApiResponseCliente {
		public boolean success;
		public int status;
		public String message;
		public String correlationId;
		public ClienteDto data;
	}

	@SuppressWarnings("unused")
	static class CuentaDto {
		public UUID id;
		public String numeroCuenta;
		public String tipo;
		public BigDecimal saldoInicial;
		public BigDecimal saldoActual;
		public boolean estado;
		public UUID clienteId;
	}

	@SuppressWarnings("unused")
	static class ClienteDto {
		public UUID id;
		public String identificacion;
		public String nombre;
		public String genero;
		public Integer edad;
		public String direccion;
		public String telefono;
		public Boolean estado;
	}

	private ClienteReporteItem fetchClienteSafe(UUID clienteId) {
		if (clienteId == null) return null;
		try {
			String url = clientesBaseUrl + "/clientes/" + clienteId;
			ResponseEntity<ApiResponseCliente> resp = restTemplate.getForEntity(url, ApiResponseCliente.class);
			ApiResponseCliente body = resp.getBody();
			if (body == null || body.data == null) return null;

			ClienteDto c = body.data;
			return new ClienteReporteItem(
					c.id,
					c.identificacion,
					c.nombre,
					c.genero,
					c.edad,
					c.direccion,
					c.telefono,
					c.estado
			);
		} catch (Exception ex) {
			// Resiliente: si ms-clientes no está disponible / 404, no rompemos el reporte
			return null;
		}
	}
}
