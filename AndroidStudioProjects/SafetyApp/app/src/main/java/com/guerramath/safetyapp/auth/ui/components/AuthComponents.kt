package com.guerramath.safetyapp.auth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Cores padrão para a seção de autenticação
 */
object AuthColors {
    val Primary = Color(0xFF6366F1)
    val Secondary = Color(0xFF8B5CF6)
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
    val Background = Color(0xFF0F172A)
    val Surface = Color(0xFF1E293B)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA0AEC0)
    val Border = Color(0xFF334155)

    val GradientPrimary = Brush.linearGradient(
        colors = listOf(Primary, Secondary)
    )

    val GradientError = Brush.linearGradient(
        colors = listOf(Color(0xFFDC2626), Color(0xFFFCA5A5))
    )
}

/**
 * Campo de texto customizado para autenticação
 */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isError: Boolean = false,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    onImeAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) AuthColors.Error else AuthColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = AuthColors.TextSecondary
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !isPasswordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onNext = { onImeAction() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuthColors.Primary,
                unfocusedBorderColor = AuthColors.Border,
                errorBorderColor = AuthColors.Error,
                focusedLabelColor = AuthColors.Primary,
                unfocusedLabelColor = AuthColors.TextSecondary,
                errorLabelColor = AuthColors.Error,
                focusedTextColor = AuthColors.TextPrimary,
                unfocusedTextColor = AuthColors.TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = AuthColors.Error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }
    }
}

/**
 * Botão primário customizado
 */
@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthColors.Primary,
            disabledContainerColor = AuthColors.Primary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = AuthColors.TextPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = AuthColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Botão secundário de texto
 */
@Composable
fun AuthTextButton(
    text: String,
    onClick: () -> Unit,
    color: Color = AuthColors.Primary,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Mensagem de sucesso ou erro
 */
@Composable
fun AuthMessage(
    message: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isError)
                    AuthColors.Error.copy(alpha = 0.1f)
                else
                    AuthColors.Success.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isError) AuthColors.Error else AuthColors.Success,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = message,
            color = if (isError) AuthColors.Error else AuthColors.Success,
            fontSize = 14.sp
        )
    }
}

/**
 * Divisor com texto
 */
@Composable
fun AuthDivider(
    text: String = "Ou",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = AuthColors.Border,
            thickness = 1.dp
        )
        Text(
            text = text,
            color = AuthColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = AuthColors.Border,
            thickness = 1.dp
        )
    }
}