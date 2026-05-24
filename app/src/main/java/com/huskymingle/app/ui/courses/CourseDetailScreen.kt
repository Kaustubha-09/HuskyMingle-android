package com.huskymingle.app.ui.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.ui.components.HMPrimaryButton
import com.huskymingle.app.ui.components.HMSecondaryButton
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(courseCode: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val app = remember { context.applicationContext as HuskyMingleApp }
    val scope = rememberCoroutineScope()
    val course = remember(courseCode) { app.courseCatalog.byCode(courseCode) }
    val enrolledIds by app.userPreferences.enrolledCourseIds.collectAsState(initial = emptySet())
    val isEnrolled = course?.id in enrolledIds

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(course?.code ?: "Course", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            )
        }
    ) { padding ->
        if (course == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Course not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(HMTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(HMTheme.radius.pill))
                    .background(HuskyRed.copy(alpha = 0.12f))
                    .padding(PaddingValues(horizontal = 12.dp, vertical = 4.dp)),
            ) {
                Text(course.department, color = HuskyRed, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            }

            Text(
                text = course.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = course.code,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                LabelValue(label = "Credits", value = "${course.credits}")
                if (course.instructor.isNotBlank()) {
                    LabelValue(label = "Instructor", value = course.instructor)
                }
            }

            if (course.description.isNotBlank()) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(course.description, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(HMTheme.spacing.md))

            if (isEnrolled) {
                HMSecondaryButton(
                    label = "Drop course",
                    onClick = { scope.launch { app.userPreferences.toggleEnrolledCourse(course.id) } },
                )
            } else {
                HMPrimaryButton(
                    label = "Enroll",
                    onClick = { scope.launch { app.userPreferences.toggleEnrolledCourse(course.id) } },
                )
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
