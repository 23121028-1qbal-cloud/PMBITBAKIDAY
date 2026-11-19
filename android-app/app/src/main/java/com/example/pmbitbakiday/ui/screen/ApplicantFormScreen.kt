package com.example.pmbitbakiday.ui.screen

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pmbitbakiday.data.local.db.ApplicantLocalEntity
import com.example.pmbitbakiday.ui.theme.CharcoalBackground
import com.example.pmbitbakiday.ui.theme.CoralRed
import com.example.pmbitbakiday.ui.theme.LimePastel
import com.example.pmbitbakiday.ui.theme.PastelYellow
import com.example.pmbitbakiday.ui.theme.SurfacePrimary
import com.example.pmbitbakiday.ui.theme.SurfaceSecondary
import com.example.pmbitbakiday.ui.theme.TextMuted
import com.example.pmbitbakiday.ui.theme.TextOnBrightAccent
import com.example.pmbitbakiday.ui.theme.TextPrimaryOnDark
import com.example.pmbitbakiday.ui.theme.TextSecondaryOnDark
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantFormScreen(
    applicant: ApplicantLocalEntity? = null,
    onBack: () -> Unit,
    onSave: (
        id: Long,
        fullName: String,
        nik: String,
        birthPlace: String,
        birthDate: String,
        gender: String,
        phone: String,
        address: String,
        studyProgram: String,
        photoUri: String?,
        isAgreed: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    val isEditMode = applicant != null

    // State Inputan
    var fullName by remember { mutableStateOf(applicant?.fullName ?: "") }
    var nik by remember { mutableStateOf(applicant?.nik ?: "") }
    var birthPlace by remember { mutableStateOf(applicant?.birthPlace ?: "") }
    var birthDate by remember { mutableStateOf(applicant?.birthDate ?: "") }
    var gender by remember { mutableStateOf(applicant?.gender ?: "Laki-laki") }
    var phone by remember { mutableStateOf(applicant?.phone ?: "") }
    var address by remember { mutableStateOf(applicant?.address ?: "") }
    var studyProgram by remember { mutableStateOf(applicant?.studyProgram ?: "Teknik Informatika") }
    var photoUriString by remember { mutableStateOf(applicant?.photoUri) }
    var isAgreed by remember { mutableStateOf(applicant?.isAgreed ?: false) }

    // Dropdown state
    var expandedDropdown by remember { mutableStateOf(false) }
    val studyPrograms = listOf(
        "Teknik Informatika",
        "Sistem Informasi",
        "Bisnis Digital",
        "Manajemen Informatika"
    )

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUriString = uri.toString()
            Toast.makeText(context, "Foto berhasil dipilih", Toast.LENGTH_SHORT).show()
        }
    }

    // DatePicker Dialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            birthDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR) - 18,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        containerColor = CharcoalBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Data Pendaftar" else "Formulir Pendaftaran PMB",
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
        ) {
            // Coral Panel Overlay Card (Inspired by Reference Image Form Sheet)
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                color = CoralRed
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isEditMode) "Perbarui data calon mahasiswa:" else "Hey, Isi formulir pendaftaran baru",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryOnDark
                    )

                    // Input TextField Custom Colors
                    val formTextFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfacePrimary,
                        unfocusedContainerColor = SurfacePrimary,
                        disabledContainerColor = SurfacePrimary,
                        focusedBorderColor = PastelYellow,
                        unfocusedBorderColor = SurfaceSecondary,
                        focusedLabelColor = TextPrimaryOnDark,
                        unfocusedLabelColor = TextSecondaryOnDark,
                        focusedTextColor = TextPrimaryOnDark,
                        unfocusedTextColor = TextPrimaryOnDark
                    )

                    // 1. INPUT TEKS: Nama Lengkap
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nama Lengkap") },
                        colors = formTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 2. INPUT ANGKA: NIK (16 digit)
                    OutlinedTextField(
                        value = nik,
                        onValueChange = { if (it.length <= 16 && it.all { char -> char.isDigit() }) nik = it },
                        label = { Text("NIK / KTP (16 Digit)") },
                        colors = formTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 3. INPUT TEKS: Tempat Lahir
                    OutlinedTextField(
                        value = birthPlace,
                        onValueChange = { birthPlace = it },
                        label = { Text("Tempat Lahir") },
                        colors = formTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 4. INPUT TANGGAL: Tanggal Lahir (DatePicker)
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tanggal Lahir") },
                        colors = formTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Pilih Tanggal",
                                    tint = PastelYellow
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                    )

                    // 5. INPUT PILIHAN (RADIO BUTTON): Jenis Kelamin
                    Column {
                        Text(
                            text = "Jenis Kelamin:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryOnDark
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { gender = "Laki-laki" }
                            ) {
                                RadioButton(
                                    selected = gender == "Laki-laki",
                                    onClick = { gender = "Laki-laki" },
                                    colors = RadioButtonDefaults.colors(selectedColor = PastelYellow)
                                )
                                Text(text = "Laki-laki", color = TextPrimaryOnDark)
                            }

                            Spacer(modifier = Modifier.width(24.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { gender = "Perempuan" }
                            ) {
                                RadioButton(
                                    selected = gender == "Perempuan",
                                    onClick = { gender = "Perempuan" },
                                    colors = RadioButtonDefaults.colors(selectedColor = PastelYellow)
                                )
                                Text(text = "Perempuan", color = TextPrimaryOnDark)
                            }
                        }
                    }

                    // 6. INPUT ANGKA: Nomor HP/WhatsApp
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Nomor HP / WhatsApp") },
                        colors = formTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 7. INPUT TEKS: Alamat Domisili
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Alamat Domisili Lengkap") },
                        colors = formTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    // 8. INPUT PILIHAN (DROPDOWN): Program Studi
                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = studyProgram,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Program Studi Pilihan") },
                            colors = formTextFieldColors,
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.background(SurfacePrimary)
                        ) {
                            studyPrograms.forEach { program ->
                                DropdownMenuItem(
                                    text = { Text(program, color = TextPrimaryOnDark) },
                                    onClick = {
                                        studyProgram = program
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // 9. INPUT GAMBAR: Pas Foto
                    Column {
                        Text(
                            text = "Pas Foto Calon Mahasiswa:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryOnDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryOnDark),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = PastelYellow)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Pilih Berkas Foto", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (photoUriString != null) "✓ Foto Terpilih" else "Belum ada foto",
                                color = if (photoUriString != null) LimePastel else TextSecondaryOnDark,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 10. INPUT CHECKBOX: Persetujuan Ketentuan
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isAgreed = !isAgreed },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isAgreed,
                            onCheckedChange = { isAgreed = it },
                            colors = CheckboxDefaults.colors(checkedColor = PastelYellow, checkmarkColor = TextOnBrightAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Saya menyatakan bahwa seluruh data yang diisikan adalah benar dan siap mematuhi peraturan PMB ITB Akiday.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryOnDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // TOMBOL UTAMA PILL
                    Button(
                        onClick = {
                            onSave(
                                applicant?.id ?: 0L,
                                fullName,
                                nik,
                                birthPlace,
                                birthDate,
                                gender,
                                phone,
                                address,
                                studyProgram,
                                photoUriString,
                                isAgreed
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CharcoalBackground,
                            contentColor = TextPrimaryOnDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = PastelYellow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditMode) "Perbarui Data Pendaftar" else "Simpan Pendaftaran Baru",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
