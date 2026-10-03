package com.game.dungeon.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.game.dungeon.R
import com.game.dungeon.ui.theme.*

@Composable
fun SupportIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PixelButton(
        label = "❓",
        onClick = onClick,
        modifier = modifier.size(32.dp),
        horizontalPadding = 0.dp,
        verticalPadding = 0.dp
    )
}

@Composable
fun SupportDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.90f)
                .widthIn(max = 450.dp)
                .wrapContentHeight()
                .background(BgDarkest)
        ) {
            Column(Modifier.padding(all = 20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Text(safeStringResource(R.string.support_feedback_title), style = PixelHeading)
                    PixelButton(
                        label = "X",
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp),
                        horizontalPadding = 0.dp,
                        verticalPadding = 0.dp
                    )
                }
                Spacer(Modifier.height(12.dp))
                PixelDivider()
                Spacer(Modifier.height(16.dp))
                
                Text(safeStringResource(R.string.help_improve_game), style = PixelGold, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    safeStringResource(R.string.support_email_desc),
                    style = PixelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
                
                Spacer(Modifier.height(16.dp))
                PixelButton(
                    label = safeStringResource(R.string.btn_send_feedback),
                    onClick = {
                        try {
                            val discordUrl = "https://discord.gg/nmdKJHG2y"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(discordUrl))
                            context.startActivity(intent)
                        } catch (_: Exception) {
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                )
                
                Spacer(Modifier.height(24.dp))
                Text(safeStringResource(R.string.love_final_dungeon), style = PixelGold, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    safeStringResource(R.string.rate_app_desc),
                    style = PixelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
                
                Spacer(Modifier.height(16.dp))
                PixelButton(
                    label = safeStringResource(R.string.btn_rate_store),
                    onClick = {
                        val packageName = context.packageName
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                )
            }
        }
    }
}
