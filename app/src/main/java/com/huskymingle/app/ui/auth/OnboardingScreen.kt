package com.huskymingle.app.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.huskymingle.app.ui.theme.HuskyGold
import com.huskymingle.app.ui.theme.HuskyRed

private val INTERESTS = listOf(
    "Technology", "Engineering", "Business", "Arts", "Music", "Sports", "Gaming",
    "Politics", "Science", "Math", "Literature", "Film", "Travel", "Food",
    "Fashion", "Photography", "Fitness", "Medicine", "Law", "Education"
)

private val SKILLS = listOf(
    "Python", "Java", "Kotlin", "Swift", "JavaScript", "React", "Machine Learning",
    "Data Science", "Design", "Marketing", "Writing", "Public Speaking", "Leadership",
    "Research", "Project Management", "Cybersecurity", "Cloud", "DevOps"
)

private val LANGUAGES = listOf(
    "English", "Spanish", "Mandarin", "Hindi", "French", "German", "Portuguese",
    "Japanese", "Korean", "Arabic", "Russian", "Italian", "Bengali", "Urdu"
)

@Composable
fun OnboardingScreen(viewModel: AuthViewModel) {
    var step by remember { mutableIntStateOf(0) }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    var selectedLanguages by remember { mutableStateOf(setOf<String>()) }
    var major by remember { mutableStateOf("") }
    var graduationYear by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val steps = listOf("Interests", "Skills", "Languages", "Academic Info")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Set Up Your Profile",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = HuskyRed
        )
        Text(
            text = "Step ${step + 1} of ${steps.size}: ${steps[step]}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        LinearProgressIndicator(
            progress = { (step + 1).toFloat() / steps.size },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = HuskyRed,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(targetState = step, label = "onboarding_step") { currentStep ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (currentStep) {
                    0 -> ChipSelector(
                        title = "What are your interests?",
                        subtitle = "Select at least 3",
                        options = INTERESTS,
                        selected = selectedInterests,
                        onToggle = { item ->
                            selectedInterests = if (item in selectedInterests)
                                selectedInterests - item else selectedInterests + item
                        }
                    )
                    1 -> ChipSelector(
                        title = "What are your skills?",
                        subtitle = "Select all that apply",
                        options = SKILLS,
                        selected = selectedSkills,
                        onToggle = { item ->
                            selectedSkills = if (item in selectedSkills)
                                selectedSkills - item else selectedSkills + item
                        }
                    )
                    2 -> ChipSelector(
                        title = "Languages you speak",
                        subtitle = "Select all that apply",
                        options = LANGUAGES,
                        selected = selectedLanguages,
                        onToggle = { item ->
                            selectedLanguages = if (item in selectedLanguages)
                                selectedLanguages - item else selectedLanguages + item
                        }
                    )
                    3 -> Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Tell us about your studies", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        OutlinedTextField(
                            value = major,
                            onValueChange = { major = it },
                            label = { Text("Major / Program") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = graduationYear,
                            onValueChange = { if (it.length <= 4) graduationYear = it },
                            label = { Text("Expected Graduation Year") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (step > 0) {
                OutlinedButton(
                    onClick = { step-- },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back")
                }
            }

            Button(
                onClick = {
                    if (step < steps.size - 1) {
                        step++
                    } else {
                        viewModel.completeOnboarding(
                            interests = selectedInterests.toList(),
                            skills = selectedSkills.toList(),
                            languages = selectedLanguages.toList(),
                            major = major,
                            graduationYear = graduationYear.toIntOrNull()
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                enabled = !isLoading && (step != 0 || selectedInterests.size >= 3),
                colors = ButtonDefaults.buttonColors(containerColor = HuskyRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (step < steps.size - 1) "Next" else "Finish", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipSelector(
    title: String,
    subtitle: String,
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option in selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggle(option) },
                    label = { Text(option) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HuskyRed,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        selectedLeadingIconColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
        }
    }
}
