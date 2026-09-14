package com.clickbus.challenge.indoor

/** Temporary label-to-node bridge while the terminal JSON categories are being completed. */
object DemoMapLocations {
    const val DEFAULT_ORIGIN_ID = "TIETE-TER-ENTRADA-002"

    fun originId(label: String?): String = when (label) {
        "Entrada Principal (Norte)" -> "TIETE-TER-ENTRADA-001"
        "Entrada Leste" -> "TIETE-TER-ENTRADA-003"
        "Táxi / Aplicativos" -> "TIETE-TER-SERVICO-017"
        else -> DEFAULT_ORIGIN_ID
    }

    fun destinationId(label: String): String = when {
        label.equals("Piso de Alimentação", ignoreCase = true) -> "TIETE-TER-SERVICO-031"
        label.equals("Bilheteria", ignoreCase = true) -> "TIETE-TER-SERVICO-001"
        label.equals("Guarda-volumes", ignoreCase = true) -> "TIETE-TER-SERVICO-020"
        label.startsWith("Plataforma") -> platformFallback(label)
        else -> "TIETE-TER-SERVICO-001"
    }

    private fun platformFallback(label: String): String {
        val index = label.substringAfterLast(' ').toIntOrNull()?.minus(1) ?: 0
        val temporaryNodes = listOf(
            "TIETE-TER-SERVICO-024", "TIETE-TER-SERVICO-026", "TIETE-TER-SERVICO-030",
            "TIETE-TER-SERVICO-014", "TIETE-TER-SERVICO-013", "TIETE-TER-SERVICO-019",
            "TIETE-TER-SERVICO-020", "TIETE-TER-SERVICO-021", "TIETE-TER-SERVICO-022",
            "TIETE-TER-SERVICO-009", "TIETE-TER-SERVICO-010", "TIETE-TER-SERVICO-011",
        )
        return temporaryNodes.getOrElse(index) { "TIETE-TER-SERVICO-001" }
    }
}
