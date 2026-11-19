package com.example.pmbitbakiday.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pmbitbakiday.data.local.db.ApplicantLocalEntity
import com.example.pmbitbakiday.ui.theme.CharcoalBackground
import com.example.pmbitbakiday.ui.theme.LimePastel
import com.example.pmbitbakiday.ui.theme.PastelYellow
import com.example.pmbitbakiday.ui.theme.StatusError
import com.example.pmbitbakiday.ui.theme.SurfacePrimary
import com.example.pmbitbakiday.ui.theme.SurfaceSecondary
import com.example.pmbitbakiday.ui.theme.TextMuted
import com.example.pmbitbakiday.ui.theme.TextOnBrightAccent
import com.example.pmbitbakiday.ui.theme.TextPrimaryOnDark
import com.example.pmbitbakiday.ui.theme.TextSecondaryOnDark
import com.example.pmbitbakiday.ui.theme.WarmCream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantListScreen(
    applicants: List<ApplicantLocalEntity>,
    isLoggedIn: Boolean = false,
    userEmail: String? = null,
    onlineStatus: String? = null,
    onAddNew: () -> Unit,
    onViewDetail: (ApplicantLocalEntity) -> Unit = {},
    onEdit: (ApplicantLocalEntity) -> Unit,
    onDelete: (Long) -> Unit,
    onNavigateToAuth: () -> Unit = {},
    onNavigateToStatus: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var applicantToDelete by remember { mutableStateOf<ApplicantLocalEntity?>(null) }

    Scaffold(
        containerColor = CharcoalBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PMB ITB Akiday",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimaryOnDark
                        )
                        Text(
                            text = if (isLoggedIn) "Akun: ${userEmail ?: "Online"}" else "Beranda Calon Mahasiswa",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryOnDark
                        )
                    }
                },
                actions = {
                    // Action button "Daftar Baru" pill button in header
                    Button(
                        onClick = onAddNew,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PastelYellow,
                            contentColor = TextOnBrightAccent
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Tambah", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    if (isLoggedIn) {
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Keluar Akun",
                                tint = TextPrimaryOnDark
                            )
                        }
                    } else {
                        IconButton(onClick = onNavigateToAuth) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Login / Registrasi",
                                tint = TextPrimaryOnDark
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Header Status Card (Prominent Rounded Card)
                    val cardBgColor = if (isLoggedIn) LimePastel else PastelYellow
                    val currentStatusLabel = onlineStatus?.uppercase() ?: if (isLoggedIn) "TERHUBUNG (DRAFT)" else "OFFLINE (SQLite)"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = TextOnBrightAccent.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "STATUS PENDAFTARAN",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnBrightAccent
                                    )
                                }

                                Text(
                                    text = currentStatusLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnBrightAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (isLoggedIn) (userEmail ?: "Calon Mahasiswa") else "Pendaftaran Offline PMB",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextOnBrightAccent
                            )

                            Text(
                                text = if (isLoggedIn) "Data Anda tersinkronisasi otomatis dengan server REST API PMB ITB Akiday."
                                else "Masuk akun untuk sinkronisasi otomatis berkas pendaftaran dengan REST API.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextOnBrightAccent.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = if (isLoggedIn) onNavigateToStatus else onNavigateToAuth,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TextOnBrightAccent,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    text = if (isLoggedIn) "Lihat Status PMB" else "Masuk Akun Online",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Progress Tahapan PMB (Stepper Cards)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Tahapan Pendaftaran PMB",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryOnDark
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PmbStageChip(
                                stageNumber = "1",
                                label = "Akun",
                                isDone = isLoggedIn,
                                modifier = Modifier.weight(1f)
                            )
                            PmbStageChip(
                                stageNumber = "2",
                                label = "Data",
                                isDone = applicants.isNotEmpty(),
                                modifier = Modifier.weight(1f)
                            )
                            PmbStageChip(
                                stageNumber = "3",
                                label = "Dokumen",
                                isDone = false,
                                modifier = Modifier.weight(1f)
                            )
                            PmbStageChip(
                                stageNumber = "4",
                                label = "Peninjauan",
                                isDone = onlineStatus == "submitted" || onlineStatus == "review" || onlineStatus == "accepted",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Header Section Lista Pendaftar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Calon Mahasiswa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryOnDark
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceSecondary
                        ) {
                            Text(
                                text = "${applicants.size} Pendaftar",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryOnDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (applicants.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    tint = PastelYellow
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Belum Ada Data Pendaftar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryOnDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tekan tombol '+ Tambah' di kanan atas untuk mengisi formulir pendaftaran calon mahasiswa.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(applicants, key = { _, item -> item.id }) { index, applicant ->
                        val cardColors = listOf(LimePastel, PastelYellow, WarmCream)
                        val bgColor = cardColors[index % cardColors.size]

                        PmbApplicantCard(
                            applicant = applicant,
                            backgroundColor = bgColor,
                            onViewDetail = { onViewDetail(applicant) },
                            onEdit = { onEdit(applicant) },
                            onDelete = { applicantToDelete = applicant }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Space for floating bottom nav
                }
            }

            // Dialog Konfirmasi Hapus
            applicantToDelete?.let { target ->
                AlertDialog(
                    onDismissRequest = { applicantToDelete = null },
                    containerColor = SurfacePrimary,
                    titleContentColor = TextPrimaryOnDark,
                    textContentColor = TextSecondaryOnDark,
                    title = { Text(text = "Hapus Data Pendaftar?") },
                    text = {
                        Text(text = "Apakah Anda yakin ingin menghapus data calon mahasiswa '${target.fullName}' (NIK: ${target.nik}) dari database SQLite?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDelete(target.id)
                                applicantToDelete = null
                            }
                        ) {
                            Text(text = "Hapus", color = StatusError, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { applicantToDelete = null }) {
                            Text(text = "Batal", color = TextSecondaryOnDark)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PmbStageChip(
    stageNumber: String,
    label: String,
    isDone: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isDone) LimePastel else SurfaceSecondary,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isDone) TextOnBrightAccent else SurfacePrimary),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = LimePastel,
                        modifier = Modifier.size(12.dp)
                    )
                } else {
                    Text(
                        text = stageNumber,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryOnDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isDone) TextOnBrightAccent else TextSecondaryOnDark,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PmbApplicantCard(
    applicant: ApplicantLocalEntity,
    backgroundColor: Color,
    onViewDetail: () -> Unit = {},
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(TextOnBrightAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = TextOnBrightAccent
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = applicant.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextOnBrightAccent
                    )
                    Text(
                        text = "NIK: ${applicant.nik}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextOnBrightAccent.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Prodi: ${applicant.studyProgram}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextOnBrightAccent
                    )
                }

                // Circular Action Buttons (Detail, Edit, Delete)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TextOnBrightAccent.copy(alpha = 0.15f))
                        .clickable { onViewDetail() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Lihat Detail",
                        tint = TextOnBrightAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TextOnBrightAccent.copy(alpha = 0.15f))
                        .clickable { onEdit() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Data",
                        tint = TextOnBrightAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StatusError.copy(alpha = 0.25f))
                        .clickable { onDelete() },
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

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${applicant.gender} | ${applicant.birthPlace}, ${applicant.birthDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextOnBrightAccent.copy(alpha = 0.8f)
                )
                Text(
                    text = "WA: ${applicant.phone}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextOnBrightAccent.copy(alpha = 0.8f)
                )
            }
        }
    }
}
