package com.example.pmbitbakiday.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pmbitbakiday.data.local.db.ApplicantLocalEntity
import com.example.pmbitbakiday.ui.theme.CharcoalBackground
import com.example.pmbitbakiday.ui.theme.LimePastel
import com.example.pmbitbakiday.ui.theme.PastelYellow
import com.example.pmbitbakiday.ui.theme.StatusError
import com.example.pmbitbakiday.ui.theme.StatusSuccess
import com.example.pmbitbakiday.ui.theme.SurfacePrimary
import com.example.pmbitbakiday.ui.theme.TextMuted
import com.example.pmbitbakiday.ui.theme.TextOnBrightAccent
import com.example.pmbitbakiday.ui.theme.TextPrimaryOnDark
import com.example.pmbitbakiday.ui.theme.TextSecondaryOnDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantDetailScreen(
    applicant: ApplicantLocalEntity?,
    isLoggedIn: Boolean = false,
    userEmail: String? = null,
    onBack: () -> Unit,
    onEdit: (ApplicantLocalEntity) -> Unit,
    onDelete: (Long) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CharcoalBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Calon Mahasiswa",
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
                    if (applicant != null) {
                        IconButton(onClick = { onEdit(applicant) }) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PastelYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Data",
                                    tint = TextOnBrightAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(StatusError.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Data",
                                    tint = StatusError,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CharcoalBackground
                )
            )
        }
    ) { paddingValues ->
        if (applicant == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Data pendaftar tidak ditemukan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted
                )
            }
            return@Scaffold
        }

        val formattedDate = remember(applicant.createdAt) {
            try {
                val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))
                sdf.format(Date(applicant.createdAt))
            } catch (_: Exception) {
                "-"
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Profile Card (Pastel Yellow / Lime)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PastelYellow)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(TextOnBrightAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = TextOnBrightAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = applicant.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextOnBrightAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TextOnBrightAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = applicant.studyProgram,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextOnBrightAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "NIK: ${applicant.nik}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextOnBrightAccent.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Section 1: Data Pribadi
            DetailSectionCard(title = "Data Pribadi") {
                DetailItemRow(
                    icon = Icons.Default.Person,
                    label = "Nama Lengkap",
                    value = applicant.fullName
                )
                DetailItemRow(
                    icon = Icons.Default.Badge,
                    label = "NIK (KTP)",
                    value = applicant.nik
                )
                DetailItemRow(
                    icon = Icons.Default.Info,
                    label = "Tempat, Tanggal Lahir",
                    value = "${applicant.birthPlace}, ${applicant.birthDate}"
                )
                DetailItemRow(
                    icon = Icons.Default.Person,
                    label = "Jenis Kelamin",
                    value = applicant.gender
                )
            }

            // Section 2: Kontak & Domisili
            DetailSectionCard(title = "Kontak & Alamat Domisili") {
                DetailItemRow(
                    icon = Icons.Default.Call,
                    label = "Nomor HP / WhatsApp",
                    value = applicant.phone
                )
                DetailItemRow(
                    icon = Icons.Default.Home,
                    label = "Alamat Domisili",
                    value = applicant.address
                )
            }

            // Section 3: Program Studi Pilihan
            DetailSectionCard(title = "Program Studi Pilihan") {
                DetailItemRow(
                    icon = Icons.Default.School,
                    label = "Program Studi",
                    value = applicant.studyProgram
                )
            }

            // Section 4: Berkas & Persetujuan
            DetailSectionCard(title = "Berkas & Persetujuan") {
                DetailItemRow(
                    icon = if (applicant.photoUri != null) Icons.Default.CheckCircle else Icons.Default.Warning,
                    label = "Pas Foto",
                    value = if (applicant.photoUri != null) "Telah diunggah (${applicant.photoUri})" else "Belum diunggah foto"
                )
                DetailItemRow(
                    icon = if (applicant.isAgreed) Icons.Default.CheckCircle else Icons.Default.Warning,
                    label = "Persetujuan Ketentuan",
                    value = if (applicant.isAgreed) "Setuju & Validated (Diisi oleh pemohon)" else "Belum menyetujui"
                )
            }

            // Section 5: Informasi Penyimpanan & Status CRUD
            DetailSectionCard(title = "Informasi Penyimpanan & Sync") {
                DetailItemRow(
                    icon = Icons.Default.Info,
                    label = "ID Lokal SQLite",
                    value = "#${applicant.id}"
                )
                DetailItemRow(
                    icon = Icons.Default.Info,
                    label = "Waktu Pendaftaran",
                    value = formattedDate
                )
                DetailItemRow(
                    icon = Icons.Default.Info,
                    label = "Status Integrasi Server",
                    value = if (isLoggedIn) "Tersinkronisasi dengan REST API (Akun: ${userEmail ?: "Online"})" else "Tersimpan Lokal di SQLite HP (Mode Offline)"
                )
            }

            // Bottom Action Pill Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Hapus", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onEdit(applicant) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PastelYellow,
                        contentColor = TextOnBrightAccent
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Edit Data", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Dialog Konfirmasi Hapus Data
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = SurfacePrimary,
                titleContentColor = TextPrimaryOnDark,
                textContentColor = TextSecondaryOnDark,
                title = { Text(text = "Hapus Data Pendaftar?") },
                text = {
                    Text(text = "Apakah Anda yakin ingin menghapus data calon mahasiswa '${applicant.fullName}' (NIK: ${applicant.nik}) dari database SQLite?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDelete(applicant.id)
                            onBack()
                        }
                    ) {
                        Text(text = "Hapus", color = StatusError, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(text = "Batal", color = TextSecondaryOnDark)
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PastelYellow
            )
            content()
        }
    }
}

@Composable
private fun DetailItemRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(PastelYellow.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = PastelYellow
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextPrimaryOnDark
            )
        }
    }
}
