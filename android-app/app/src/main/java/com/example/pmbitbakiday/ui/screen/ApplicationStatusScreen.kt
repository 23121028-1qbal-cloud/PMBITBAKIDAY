package com.example.pmbitbakiday.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pmbitbakiday.data.model.StatusHistoryDto
import com.example.pmbitbakiday.ui.theme.CharcoalBackground
import com.example.pmbitbakiday.ui.theme.CoralRed
import com.example.pmbitbakiday.ui.theme.LimePastel
import com.example.pmbitbakiday.ui.theme.PastelYellow
import com.example.pmbitbakiday.ui.theme.SoftOrange
import com.example.pmbitbakiday.ui.theme.StatusError
import com.example.pmbitbakiday.ui.theme.StatusInfo
import com.example.pmbitbakiday.ui.theme.StatusSuccess
import com.example.pmbitbakiday.ui.theme.StatusWarning
import com.example.pmbitbakiday.ui.theme.SurfacePrimary
import com.example.pmbitbakiday.ui.theme.SurfaceSecondary
import com.example.pmbitbakiday.ui.theme.TextMuted
import com.example.pmbitbakiday.ui.theme.TextOnBrightAccent
import com.example.pmbitbakiday.ui.theme.TextPrimaryOnDark
import com.example.pmbitbakiday.ui.theme.TextSecondaryOnDark
import com.example.pmbitbakiday.ui.viewmodel.ApplicantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationStatusScreen(
    viewModel: ApplicantViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val myApp by viewModel.myApplication.collectAsState()
    val histories by viewModel.statusHistory.collectAsState()
    val isLoading by viewModel.isOnlineLoading.collectAsState()

    Scaffold(
        containerColor = CharcoalBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Status Pendaftaran Online",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimaryOnDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfacePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = TextPrimaryOnDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadOnlineData() }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfacePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Muat Ulang Data",
                                tint = PastelYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CharcoalBackground
                )
            )
        }
    ) { paddingValues ->
        if (myApp == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfacePrimary)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PastelYellow,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Belum Ada Pengajuan Online",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryOnDark
                        )
                        Text(
                            text = "Silakan isi formulir pendaftaran calon mahasiswa untuk memulai pengajuan pendaftaran online ke server PMB ITB Akiday.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelYellow,
                                contentColor = TextOnBrightAccent
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Buka Formulir Pendaftaran", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            val app = myApp!!
            val statusColor = when (app.status) {
                "accepted" -> StatusSuccess
                "rejected" -> StatusError
                "revision" -> StatusWarning
                "review" -> StatusInfo
                "submitted" -> StatusInfo
                else -> TextMuted
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Status Header Card (Pastel Lime / Yellow)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LimePastel),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = app.registrationNumber,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnBrightAccent
                                )
                                Surface(
                                    color = TextOnBrightAccent,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = app.status.uppercase(),
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Gelombang: ${app.wave?.name ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextOnBrightAccent
                            )
                            Text(
                                text = "Program Studi: ${app.studyProgram?.name ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextOnBrightAccent
                            )
                            if (app.submittedAt != null) {
                                Text(
                                    text = "Dikirim Pada: ${app.submittedAt}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextOnBrightAccent.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Banner Instruksi Khusus Revisi (Coral Red / Soft Orange Surface)
                if (app.status == "revision") {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SoftOrange),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = TextOnBrightAccent
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Perlu Perbaikan Berkas",
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnBrightAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Panitia telah meninjau pengajuan Anda dan meminta perbaikan data/dokumen. Silakan periksa riwayat catatan panitia di bawah, lalu kirim ulang perbaikan.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextOnBrightAccent.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        viewModel.resubmitOnlineApplication(app.id) { _, msg ->
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = !isLoading,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TextOnBrightAccent,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Kirim Ulang Perbaikan (Resubmit)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Banner Sukses Jika Diterima
                if (app.status == "accepted") {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = StatusSuccess),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TextOnBrightAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Selamat! Pendaftaran Anda telah dinyatakan DITERIMA di ITB Akiday.",
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnBrightAccent,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Riwayat Status Pendaftaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryOnDark
                    )
                }

                if (histories.isEmpty()) {
                    item {
                        Text(
                            text = "Belum ada riwayat status tercatat.",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    items(histories) { history ->
                        StatusHistoryItem(history = history)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun StatusHistoryItem(history: StatusHistoryDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${history.fromStatus ?: "Awal"} → ${history.toStatus.uppercase()}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PastelYellow
                )
                if (history.changedAt != null) {
                    Text(
                        text = history.changedAt.substringBefore("T"),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
            if (!history.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Catatan Panitia: ${history.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryOnDark
                )
            }
        }
    }
}
