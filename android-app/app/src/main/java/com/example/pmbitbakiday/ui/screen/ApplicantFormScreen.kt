package com.example.pmbitbakiday.ui.screen

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pmbitbakiday.data.local.db.ApplicantLocalEntity
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

    // Image picker launcher (Input Gambar)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUriString = uri.toString()
            Toast.makeText(context, "Foto berhasil dipilih", Toast.LENGTH_SHORT).show()
        }
    }

    // DatePicker Dialog (Input Tanggal)
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Data Pendaftar" else "Formulir Pendaftaran Baru",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Lengkapi data calon mahasiswa berikut:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            // 1. INPUT TEKS: Nama Lengkap
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nama Lengkap (Input Teks)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. INPUT ANGKA: NIK (16 digit)
            OutlinedTextField(
                value = nik,
                onValueChange = { if (it.length <= 16 && it.all { char -> char.isDigit() }) nik = it },
                label = { Text("NIK / KTP (Input Angka 16 Digit)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 3. INPUT TEKS: Tempat Lahir
            OutlinedTextField(
                value = birthPlace,
                onValueChange = { birthPlace = it },
                label = { Text("Tempat Lahir (Input Teks)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 4. INPUT TANGGAL: Tanggal Lahir (DatePicker)
            OutlinedTextField(
                value = birthDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Lahir (Input Tanggal)") },
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
            )

            // 5. INPUT PILIHAN (RADIO BUTTON): Jenis Kelamin
            Column {
                Text(
                    text = "Jenis Kelamin (Input Pilihan Radio Button):",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
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
                            onClick = { gender = "Laki-laki" }
                        )
                        Text(text = "Laki-laki")
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gender = "Perempuan" }
                    ) {
                        RadioButton(
                            selected = gender == "Perempuan",
                            onClick = { gender = "Perempuan" }
                        )
                        Text(text = "Perempuan")
                    }
                }
            }

            // 6. INPUT ANGKA: Nomor HP/WhatsApp
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Nomor HP / WhatsApp (Input Angka)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 7. INPUT TEKS: Alamat Domisili
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat Lengkap (Input Teks Paragraf)") },
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
                    label = { Text("Program Studi Pilihan (Input Dropdown)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    studyPrograms.forEach { program ->
                        DropdownMenuItem(
                            text = { Text(program) },
                            onClick = {
                                studyProgram = program
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            // 9. INPUT GAMBAR: Unggah Pas Foto
            Column {
                Text(
                    text = "Pas Foto Calon Mahasiswa (Input Gambar):",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") }
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Pilih Berkas Foto")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (photoUriString != null) "✓ Foto Terpilih" else "Belum ada foto",
                        color = if (photoUriString != null) MaterialTheme.colorScheme.primary else Color.Gray,
                        style = MaterialTheme.typography.bodySmall
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
                    onCheckedChange = { isAgreed = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Saya menyatakan bahwa seluruh data yang diisikan adalah benar dan siap mematuhi peraturan PMB ITB Akiday. (Input Checkbox)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TOMBOL SIMPAN (CREATE / UPDATE)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditMode) "Perbarui Data Pendaftar" else "Simpan Pendaftaran Baru",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
