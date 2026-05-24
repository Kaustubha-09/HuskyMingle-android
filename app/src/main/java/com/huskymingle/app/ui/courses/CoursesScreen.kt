package com.huskymingle.app.ui.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.data.model.Course
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    onMenuOpen: () -> Unit = {},
    onOpenCourse: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val app = remember { context.applicationContext as HuskyMingleApp }
    val scope = rememberCoroutineScope()

    val allCourses = remember { app.courseCatalog.all() }
    val departments = remember { listOf<String?>(null) + app.courseCatalog.departments() }
    val enrolledIds by app.userPreferences.enrolledCourseIds.collectAsState(initial = emptySet())

    var selectedTab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var department by remember { mutableStateOf<String?>(null) }

    val visible = allCourses.filter { c ->
        val matchesText = query.isBlank() ||
            c.code.contains(query, ignoreCase = true) ||
            c.name.contains(query, ignoreCase = true) ||
            c.instructor.contains(query, ignoreCase = true)
        val matchesDept = department == null || c.department.equals(department, ignoreCase = true)
        val matchesTab = selectedTab == 0 || c.id in enrolledIds
        matchesText && matchesDept && matchesTab
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Courses", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuOpen) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("All courses") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Enrolled (${enrolledIds.size})") })
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search by code, title, or instructor…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HMTheme.spacing.md, vertical = HMTheme.spacing.xs),
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = HMTheme.spacing.md),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(departments) { dept ->
                    DeptChip(
                        label = dept ?: "All",
                        selected = dept == department,
                        onClick = { department = dept },
                    )
                }
            }

            if (visible.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No courses match.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(HMTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm),
                ) {
                    items(visible, key = { it.id }) { course ->
                        CourseCard(
                            course = course,
                            isEnrolled = course.id in enrolledIds,
                            onTap = { onOpenCourse(course.code) },
                            onEnrollToggle = {
                                scope.launch { app.userPreferences.toggleEnrolledCourse(course.id) }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeptChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) HuskyRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = if (selected) HuskyRed.copy(alpha = 0.4f) else Color.Transparent,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            color = if (selected) HuskyRed else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun CourseCard(
    course: Course,
    isEnrolled: Boolean,
    onTap: () -> Unit = {},
    onEnrollToggle: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(course.code, color = HuskyRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(course.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                Text("${course.credits} cr.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (course.instructor.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(course.instructor, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (course.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    course.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        course.department,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Button(
                    onClick = onEnrollToggle,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEnrolled) MaterialTheme.colorScheme.surfaceVariant else HuskyRed,
                        contentColor = if (isEnrolled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary,
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(if (isEnrolled) "Enrolled" else "Enroll", fontSize = 13.sp)
                }
            }
        }
    }
}
