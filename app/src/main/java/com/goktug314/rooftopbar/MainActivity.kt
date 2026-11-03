package com.goktug314.rooftopbar

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Ink = Color(0xFFF5F2EF)
private val Muted = Color(0xFFB2B7C7)
private val Gold = Color(0xFFE9BD78)
private val Purple = Color(0xFFAE8CFF)
private val Teal = Color(0xFF7AD7C7)
private val Panel = Color(0xD91A2234)
private val PanelRaised = Color(0xEE242D43)
private val FineLine = Color(0x50FFFFFF)

private enum class Page { Home, HowToPlay, Recipes, Game, GameOver }
private enum class MenuType { Drink, Food }

private data class MenuRecipe(
    val name: String,
    val type: MenuType,
    val icon: String,
    val accent: Color,
    val ingredients: String,
    val method: String,
)

private data class Guest(val name: String, val portrait: String, val table: String)

private val recipes = listOf(
    MenuRecipe("Strawberry Mojito", MenuType.Drink, "🍓", Color(0xFFF28C9A), "White rum, fresh strawberries, lime, mint, soda water, crushed ice.", "Muddle strawberries, lime and mint. Add rum and crushed ice, top with soda, then stir gently."),
    MenuRecipe("Blue Lagoon", MenuType.Drink, "💙", Color(0xFF63BDE7), "Vodka, blue curaçao, lemonade, lemon wheel, ice.", "Fill a highball glass with ice. Pour vodka and blue curaçao, top with lemonade, and garnish with lemon."),
    MenuRecipe("Virgin Piña Colada", MenuType.Drink, "🍍", Color(0xFFE9C56D), "Pineapple juice, coconut cream, crushed ice, pineapple wedge.", "Blend pineapple juice, coconut cream and crushed ice until smooth. Pour into a chilled glass."),
    MenuRecipe("Tropical Sunset", MenuType.Drink, "🌅", Color(0xFFFFA66D), "Orange juice, pineapple juice, grenadine, orange slice, ice.", "Pour the juices over ice. Slowly drizzle grenadine down the inside of the glass for a sunset layer."),
    MenuRecipe("Lemon Mint Cooler", MenuType.Drink, "🍋", Color(0xFF9ACD80), "Fresh lemon, mint leaves, simple syrup, sparkling water, ice.", "Muddle lemon, mint and syrup. Add ice and sparkling water, then stir and serve cold."),
    MenuRecipe("Gourmet Sliders", MenuType.Food, "🍔", Color(0xFFE9A76F), "Mini brioche buns, beef patties, cheddar, lettuce, tomato, house sauce.", "Sear the patties until cooked through. Layer with cheddar, lettuce, tomato and house sauce in toasted buns."),
    MenuRecipe("Truffle Fries", MenuType.Food, "🍟", Color(0xFFFFD16F), "Potatoes, truffle oil, parmesan, parsley, sea salt.", "Fry the potatoes until crisp. Toss with truffle oil and sea salt, then finish with parmesan and parsley."),
    MenuRecipe("Mediterranean Bruschetta", MenuType.Food, "🥖", Color(0xFFE39B71), "Sourdough, ripe tomato, basil, olive oil, garlic, feta.", "Toast garlic-rubbed sourdough. Spoon on diced tomato and basil, drizzle with olive oil, and crumble feta on top."),
    MenuRecipe("Mini Cheesecake", MenuType.Food, "🍰", Color(0xFFE5A3C7), "Cream cheese, biscuit crumb, butter, vanilla, berries.", "Press buttered biscuit crumb into mini cups. Fill with vanilla cream cheese, chill until set, and top with berries."),
)

private val guests = listOf(
    Guest("Maya", "👩🏽‍🦱", "Table 04"),
    Guest("Leo", "🧑🏻‍🦰", "Table 07"),
    Guest("Sofia", "👩🏼", "Table 02"),
    Guest("Noah", "🧔🏾", "Table 09"),
    Guest("Ava", "👩🏻‍🦰", "Table 05"),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(8, 13, 25)
        window.navigationBarColor = android.graphics.Color.rgb(8, 13, 25)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Gold,
                secondary = Purple,
                tertiary = Teal,
                background = Color(0xFF080D19),
                surface = PanelRaised,
                onBackground = Ink,
                onSurface = Ink,
            )) {
                RooftopApp()
            }
        }
    }
}

@Composable
private fun RooftopApp() {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("rooftop-cocktail-master", Context.MODE_PRIVATE) }
    val coroutineScope = rememberCoroutineScope()

    var page by remember { mutableStateOf(Page.Home) }
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(preferences.getInt("high-score", 0)) }
    var secondsLeft by remember { mutableIntStateOf(60) }
    var streak by remember { mutableIntStateOf(0) }
    var completedOrders by remember { mutableIntStateOf(0) }
    var currentRecipe by remember { mutableStateOf(recipes.random()) }
    var currentGuest by remember { mutableStateOf(guests.random()) }
    var orderStartedAt by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var feedback by remember { mutableStateOf("") }
    var feedbackIsGood by remember { mutableStateOf(true) }
    var answerLocked by remember { mutableStateOf(false) }

    fun finishShift() {
        if (score > highScore) {
            highScore = score
            preferences.edit().putInt("high-score", score).apply()
        }
        page = Page.GameOver
    }

    fun startShift() {
        score = 0
        secondsLeft = 60
        streak = 0
        completedOrders = 0
        currentRecipe = recipes.random()
        currentGuest = guests.random()
        orderStartedAt = System.currentTimeMillis()
        feedback = ""
        answerLocked = false
        page = Page.Game
    }

    LaunchedEffect(page) {
        if (page == Page.Game) {
            while (secondsLeft > 0 && page == Page.Game) {
                delay(1_000)
                secondsLeft = (secondsLeft - 1).coerceAtLeast(0)
            }
            if (page == Page.Game && secondsLeft == 0) finishShift()
        }
    }

    fun submitAnswer(selected: MenuRecipe) {
        if (page != Page.Game || answerLocked || secondsLeft <= 0) return
        answerLocked = true
        completedOrders += 1
        val additions = mutableListOf<String>()
        if (selected == currentRecipe) {
            score += 100
            streak += 1
            additions += "Correct order +100"
            if (streak % 3 == 0) {
                score += 50
                additions += "3-order streak +50"
            }
            if (System.currentTimeMillis() - orderStartedAt <= 5_000) {
                score += 25
                additions += "Fast service +25"
            }
            feedbackIsGood = true
        } else {
            score -= 20
            streak = 0
            additions += "Wrong order −20"
            feedbackIsGood = false
        }
        if (completedOrders == 10) {
            score += 100
            additions += "10 orders served +100"
        }
        feedback = additions.joinToString("  ·  ")
        coroutineScope.launch {
            delay(850)
            if (page == Page.Game && secondsLeft > 0) {
                currentRecipe = recipes.random()
                currentGuest = guests.random()
                orderStartedAt = System.currentTimeMillis()
                feedback = ""
                answerLocked = false
            }
        }
    }

    when (page) {
        Page.Home -> HomeScreen(
            highScore = highScore,
            onPlay = ::startShift,
            onHowToPlay = { page = Page.HowToPlay },
            onRecipes = { page = Page.Recipes },
        )
        Page.HowToPlay -> HowToPlayScreen(onBack = { page = Page.Home })
        Page.Recipes -> RecipeBookScreen(onBack = { page = Page.Home })
        Page.Game -> GameScreen(
            score = score,
            secondsLeft = secondsLeft,
            streak = streak,
            completedOrders = completedOrders,
            guest = currentGuest,
            order = currentRecipe,
            feedback = feedback,
            feedbackIsGood = feedbackIsGood,
            answerLocked = answerLocked,
            onBack = { page = Page.Home },
            onSelect = ::submitAnswer,
        )
        Page.GameOver -> GameOverScreen(
            score = score,
            highScore = highScore,
            orders = completedOrders,
            onPlayAgain = ::startShift,
            onHome = { page = Page.Home },
        )
    }
}

@Composable
private fun ScreenBackdrop(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        CitySkyline()
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0x4A070B16), Color(0x1A11172A), Color(0xAA080D19)))))
        content()
    }
}

@Composable
private fun CitySkyline() {
    Canvas(Modifier.fillMaxSize()) {
        drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF11152B), Color(0xFF20213C), Color(0xFF10182A), Color(0xFF111521))))
        drawCircle(color = Color(0x33E9BD78), radius = size.width * .17f, center = Offset(size.width * .83f, size.height * .17f))
        drawCircle(color = Color(0xFFFFE0A2), radius = size.width * .045f, center = Offset(size.width * .83f, size.height * .17f))
        repeat(38) { index ->
            val x = (index * 83 % 997) / 997f * size.width
            val y = (index * 47 % 260) / 1000f * size.height
            val alpha = .24f + (index % 5) * .1f
            drawCircle(color = Color.White.copy(alpha = alpha), radius = if (index % 4 == 0) 2.1f else 1.2f, center = Offset(x, y))
        }
        val baseline = size.height * .64f
        val buildingWidth = size.width / 14f
        repeat(14) { index ->
            val height = size.height * (.13f + ((index * 7 + 3) % 11) * .014f)
            val left = index * buildingWidth - 1f
            drawRect(
                color = if (index % 3 == 0) Color(0xFF111827) else Color(0xFF171C2B),
                topLeft = Offset(left, baseline - height),
                size = Size(buildingWidth + 2f, height + size.height * .43f),
            )
            val columns = 3
            val rows = 5
            repeat(columns * rows) { cell ->
                if ((cell + index * 3) % 4 != 0) {
                    val col = cell % columns
                    val row = cell / columns
                    val light = if ((cell + index) % 3 == 0) Color(0xFFA7C5E5) else Color(0xFFE9BD78)
                    drawRect(
                        color = light.copy(alpha = .22f + ((cell + index) % 3) * .1f),
                        topLeft = Offset(left + 8f + col * 13f, baseline - height + 12f + row * 17f),
                        size = Size(5f, 7f),
                    )
                }
            }
        }
        drawRect(color = Color(0xFF171927), topLeft = Offset(0f, baseline + size.height * .22f), size = Size(size.width, size.height * .14f))
        drawRect(color = Color(0xFF292536), topLeft = Offset(0f, baseline + size.height * .21f), size = Size(size.width, 8f))
    }
}

@Composable
private fun PageColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) { content() }
}

@Composable
private fun Header(title: String, onBack: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            SmallAction("‹", onBack, Modifier.size(42.dp))
            Spacer(Modifier.width(12.dp))
        } else {
            Box(Modifier.size(39.dp).clip(RoundedCornerShape(13.dp)).background(Gold), contentAlignment = Alignment.Center) {
                Text("R", color = Color(0xFF251B1A), fontWeight = FontWeight.Black, fontSize = 22.sp)
            }
            Spacer(Modifier.width(11.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title.uppercase(), color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, letterSpacing = 1.4.sp)
            Text(if (onBack == null) "ROOFTOP COCKTAIL MASTER" else "CITY LIGHTS · SERVICE ON", color = Gold, fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 1.5.sp)
        }
        if (onBack == null) Text("✦", color = Gold, fontSize = 22.sp)
    }
}

@Composable
private fun HomeScreen(highScore: Int, onPlay: () -> Unit, onHowToPlay: () -> Unit, onRecipes: () -> Unit) {
    ScreenBackdrop {
        PageColumn {
            Header("Rooftop Bar")
            Spacer(Modifier.height(4.dp))
            Column(Modifier.fillMaxWidth().weight(1f, fill = false), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("A NIGHT ABOVE THE CITY", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text("ROOFTOP", color = Ink, fontSize = 39.sp, lineHeight = 43.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                Text("COCKTAIL MASTER", color = Gold, fontSize = 23.sp, lineHeight = 29.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = .4.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(7.dp))
                Text("Run the bar. Serve the right order. Own the night.", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                RooftopIllustration(Modifier.fillMaxWidth().height(186.dp))
            }
            Surface(color = Panel, shape = RoundedCornerShape(17.dp), border = androidx.compose.foundation.BorderStroke(1.dp, FineLine), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(vertical = 14.dp, horizontal = 17.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("PERSONAL BEST", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                        Spacer(Modifier.height(3.dp))
                        Text("$highScore pts", color = Gold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("NEXT SHIFT", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                        Spacer(Modifier.height(3.dp))
                        Text("60 seconds · 9 recipes", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            AccentButton("START YOUR SHIFT", onPlay, Modifier.fillMaxWidth().height(54.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GhostButton("HOW TO PLAY", onHowToPlay, Modifier.weight(1f).height(48.dp))
                GhostButton("RECIPES", onRecipes, Modifier.weight(1f).height(48.dp))
            }
            Text("NO SIGNAL NEEDED · YOUR HIGH SCORE STAYS ON THIS DEVICE", color = Color(0xFF848A9A), fontSize = 8.sp, letterSpacing = .8.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 3.dp))
        }
    }
}

@Composable
private fun RooftopIllustration(modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(25.dp)).background(Brush.verticalGradient(listOf(Color(0xFF31314D), Color(0xFF1A2336), Color(0xFF273044)))) ) {
        Canvas(Modifier.fillMaxSize()) {
            repeat(8) { index ->
                val x = size.width * (index + 1) / 9f
                drawLine(color = Gold.copy(alpha = .45f), start = Offset(x, 0f), end = Offset(x + 18f, size.height * .65f), strokeWidth = 2f)
                drawCircle(color = Gold.copy(alpha = .7f), radius = 2.5f, center = Offset(x, 3f))
            }
            drawRect(color = Color(0xFF1A1B2B), topLeft = Offset(0f, size.height * .78f), size = Size(size.width, size.height * .22f))
            drawRect(color = Color(0xFF775C4D), topLeft = Offset(size.width * .08f, size.height * .75f), size = Size(size.width * .84f, 3f))
        }
        Row(Modifier.fillMaxSize().padding(horizontal = 27.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🍸", fontSize = 69.sp)
                Text("THE BAR", color = Gold, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🍹", fontSize = 52.sp)
                Text("CITY VIEW", color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🧑🏻‍🍳", fontSize = 55.sp)
                Text("YOUR SHIFT", color = Gold, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
            }
        }
    }
}

@Composable
private fun HowToPlayScreen(onBack: () -> Unit) {
    ScreenBackdrop {
        PageColumn {
            Header("How to play", onBack)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                Text("YOUR SHIFT, YOUR RULES", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)
                Text("Make every order count.", color = Ink, fontSize = 29.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = -.5.sp)
                Text("A guest places a food or drink order. Tap the matching menu card before the clock runs out.", color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                RuleCard("01", "Read the order", "Check the guest's requested drink or dish and table.")
                RuleCard("02", "Tap the matching recipe", "Choose the exact item from the rooftop menu. Every selection completes one order.")
                RuleCard("03", "Build your streak", "Serve quickly and keep consecutive correct orders going to stack bonus points.")
                Surface(color = Panel, shape = RoundedCornerShape(19.dp), border = androidx.compose.foundation.BorderStroke(1.dp, FineLine), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("SCORING", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.3.sp)
                        ScoreRule("Correct order", "+100")
                        ScoreRule("Wrong order", "−20")
                        ScoreRule("3 correct in a row", "+50")
                        ScoreRule("Serve in 5 seconds or less", "+25")
                        ScoreRule("Complete 10 orders", "+100")
                        Text("Bonuses stack with order points. Your best score is saved on this device.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }
            AccentButton("GOT IT", onBack, Modifier.fillMaxWidth().height(50.dp))
        }
    }
}

@Composable
private fun RuleCard(number: String, title: String, description: String) {
    Surface(color = Panel, shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, FineLine), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Text(number, color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(description, color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun ScoreRule(label: String, points: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Ink, fontSize = 12.sp)
        Text(points, color = if (points.startsWith("−")) Color(0xFFFF999D) else Teal, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun RecipeBookScreen(onBack: () -> Unit) {
    ScreenBackdrop {
        PageColumn {
            Header("Recipes", onBack)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Text("THE ROOFTOP MENU", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)
                Text("Recipes & prep", color = Ink, fontSize = 29.sp, fontWeight = FontWeight.ExtraBold)
                Text("Every guest favorite, made with a little rooftop flair.", color = Muted, fontSize = 12.sp)
                Text("SIGNATURE DRINKS", color = Teal, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp, modifier = Modifier.padding(top = 6.dp))
                recipes.filter { it.type == MenuType.Drink }.forEach { RecipeCard(it) }
                Text("ROOFTOP BITES", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp, modifier = Modifier.padding(top = 6.dp))
                recipes.filter { it.type == MenuType.Food }.forEach { RecipeCard(it) }
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: MenuRecipe) {
    Surface(color = Panel, shape = RoundedCornerShape(17.dp), border = androidx.compose.foundation.BorderStroke(1.dp, FineLine), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(recipe.icon, fontSize = 28.sp)
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text(recipe.name, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(if (recipe.type == MenuType.Drink) "SIGNATURE DRINK" else "ROOFTOP BITE", color = recipe.accent, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp)
                }
            }
            Text("INGREDIENTS", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp)
            Text(recipe.ingredients, color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
            Text("PREPARATION", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp)
            Text(recipe.method, color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun GameScreen(
    score: Int,
    secondsLeft: Int,
    streak: Int,
    completedOrders: Int,
    guest: Guest,
    order: MenuRecipe,
    feedback: String,
    feedbackIsGood: Boolean,
    answerLocked: Boolean,
    onBack: () -> Unit,
    onSelect: (MenuRecipe) -> Unit,
) {
    ScreenBackdrop {
        PageColumn {
            Header("Service round", onBack)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill("SCORE", score.toString(), Gold, Modifier.weight(1f))
                StatPill("TIME", "${secondsLeft / 60}:${(secondsLeft % 60).toString().padStart(2, '0')}", if (secondsLeft <= 10) Color(0xFFFF969C) else Ink, Modifier.weight(1f))
                StatPill("STREAK", "${streak}x", Teal, Modifier.weight(1f))
            }
            Surface(color = PanelRaised, shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66E9BD78)), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(54.dp).clip(CircleShape).background(Color(0xFF36314A)), contentAlignment = Alignment.Center) {
                        Text(guest.portrait, fontSize = 29.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${guest.name.toUpperCase()} · ${guest.table.toUpperCase()}", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("“Could I get a ${order.name}, please?”", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 21.sp)
                        Text(if (order.type == MenuType.Drink) "SIGNATURE DRINK" else "FROM THE KITCHEN", color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, modifier = Modifier.padding(top = 5.dp))
                    }
                    Text(order.icon, fontSize = 28.sp)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("TAP THE MATCHING ITEM", color = Ink, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                Text("ORDER ${completedOrders + 1}", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            feedback.takeIf { it.isNotBlank() }?.let {
                Text(it, color = if (feedbackIsGood) Teal else Color(0xFFFF999D), fontSize = 10.sp, fontWeight = FontWeight.Bold, lineHeight = 14.sp, modifier = Modifier.fillMaxWidth())
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recipes.chunked(2).forEach { rowRecipes ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowRecipes.forEach { recipe ->
                            MenuChoice(recipe, enabled = !answerLocked, modifier = Modifier.weight(1f), onClick = { onSelect(recipe) })
                        }
                        if (rowRecipes.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            Text("Serve quickly for a +25 fast-service bonus", color = Muted, fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp))
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(color = Panel, shape = RoundedCornerShape(13.dp), border = androidx.compose.foundation.BorderStroke(1.dp, FineLine), modifier = modifier) {
        Column(Modifier.padding(horizontal = 11.dp, vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(value, color = valueColor, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun MenuChoice(recipe: MenuRecipe, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        color = if (enabled) Panel else Color(0xB3171D2B),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FineLine),
        modifier = modifier.height(50.dp).clickable(enabled = enabled, onClick = onClick),
    ) {
        Row(Modifier.padding(horizontal = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(recipe.icon, fontSize = 22.sp)
            Spacer(Modifier.width(7.dp))
            Text(recipe.name, color = if (enabled) Ink else Muted, fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun GameOverScreen(score: Int, highScore: Int, orders: Int, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    ScreenBackdrop {
        PageColumn {
            Header("Shift complete")
            Spacer(Modifier.weight(.6f))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌃", fontSize = 54.sp)
                Spacer(Modifier.height(12.dp))
                Text("THE NIGHT IS YOURS", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text("Shift complete.", color = Ink, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                Surface(color = PanelRaised, shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66E9BD78)), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("FINAL SCORE", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)
                        Text("$score", color = Gold, fontSize = 56.sp, fontWeight = FontWeight.Black)
                        Text("PERSONAL BEST  ·  $highScore", color = Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ResultStat("ORDERS SERVED", orders.toString())
                            ResultStat("SHIFT LENGTH", "60 sec")
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            AccentButton("PLAY ANOTHER SHIFT", onPlayAgain, Modifier.fillMaxWidth().height(54.dp))
            GhostButton("BACK TO MAIN MENU", onHome, Modifier.fillMaxWidth().height(48.dp))
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun ResultStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
    }
}

@Composable
private fun AccentButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color(0xFF211B1B)),
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp)
    }
}

@Composable
private fun GhostButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = Panel,
        shape = RoundedCornerShape(13.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FineLine),
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = Ink, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
        }
    }
}

@Composable
private fun SmallAction(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(color = Panel, shape = RoundedCornerShape(13.dp), border = androidx.compose.foundation.BorderStroke(1.dp, FineLine), modifier = modifier.clickable(onClick = onClick)) {
        Box(contentAlignment = Alignment.Center) { Text(label, color = Gold, fontSize = 30.sp, lineHeight = 30.sp, fontWeight = FontWeight.Light) }
    }
}
