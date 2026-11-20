package dev.niza.autodroid.engine

internal fun evaluateBooleanExpression(
    expression: String,
    variables: Map<String, String>,
): Boolean {
    val resolved = variables.entries.fold(expression) { text, (name, value) -> text.replace("{$name}", value) }
    val orParts = resolved.split("||")
    if (orParts.size > 1) return orParts.any { evaluateSingleBoolean(it.trim(), variables) }
    val andParts = resolved.split("&&")
    if (andParts.size > 1) return andParts.all { evaluateSingleBoolean(it.trim(), variables) }
    return evaluateSingleBoolean(resolved.trim(), variables)
}

private fun evaluateSingleBoolean(
    expression: String,
    variables: Map<String, String>,
): Boolean {
    listOf("==", "!=", ">=", "<=", ">", "<").forEach { operator ->
        val index = expression.indexOf(operator)
        if (index > 0) {
            val left = resolveValue(expression.substring(0, index).trim(), variables)
            val right = resolveValue(expression.substring(index + operator.length).trim(), variables)
            return compareValues(left, operator, right)
        }
    }
    return resolveValue(expression, variables).toBooleanStrictOrNull() ?: false
}

private fun resolveValue(
    value: String,
    variables: Map<String, String>,
): String = variables.entries.fold(value) { text, (name, current) -> text.replace("{$name}", current) }

private fun compareValues(
    left: String,
    operator: String,
    right: String,
): Boolean {
    val leftNumber = left.toDoubleOrNull()
    val rightNumber = right.toDoubleOrNull()
    if (leftNumber != null && rightNumber != null) {
        return when (operator) {
            "==" -> leftNumber == rightNumber
            "!=" -> leftNumber != rightNumber
            ">=" -> leftNumber >= rightNumber
            "<=" -> leftNumber <= rightNumber
            ">" -> leftNumber > rightNumber
            "<" -> leftNumber < rightNumber
            else -> false
        }
    }
    return when (operator) {
        "==" -> left == right
        "!=" -> left != right
        else -> false
    }
}
