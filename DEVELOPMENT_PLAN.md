---
name: Joker's Curse — Escape Room v1.0
overview: Build a dark narrative puzzle game where the Joker has cursed 7 gothic cards and imprisoned the player in his carnival. Each card hides a completely different puzzle mechanic — memory sequences, code cracking, pattern mirroring, word unscrambling, odd-one-out, colour sequences, and a 3-step final boss. All 7 cards available from the start. 3 strikes resets any card from scratch. Break all 7 curses to escape. Built entirely in Jetpack Compose using the Jok_Ag3 asset pack. Fully offline. All content in JSON. 3-day delivery sprint.
todos:
  - id: project-setup
    content: Setup project with Compose, Room, Navigation, Gson dependencies. Copy Jok_Ag3 assets into res/drawable. Create curses.json in assets/. Configure NavHost with dark fade transitions.
    status: completed
  - id: data-models
    content: Build all data models and enums — CursedCard, PuzzleUiState, PuzzleType, PuzzlePhase, CardState. Create CurseRepository with Gson parse of curses.json.
    status: completed
    dependencies:
      - project-setup
  - id: room-db
    content: Build Room DB — CardProgress entity (cardId, isCurseBroken, bestTimeMs, totalAttempts, totalResets, brokenAt), DAO, AppDatabase.
    status: completed
    dependencies:
      - project-setup
  - id: puzzle-viewmodel
    content: Implement PuzzleViewModel with full attempt tracking, 3-strike system, reset logic, onAttemptFailed(), and StateFlow PuzzleUiState covering all 7 puzzle types.
    status: pending
    dependencies:
      - data-models
      - room-db
  - id: home-viewmodel
    content: Implement HomeViewModel — load all 7 CardState from Room, track broken count, expose card grid state, handle difficulty selection.
    status: pending
    dependencies:
      - room-db
  - id: joker-monologue
    content: Build JokerMonologue.kt reusable typewriter composable — 25ms per character LaunchedEffect, used on every Joker quote screen.
    status: pending
    dependencies:
      - project-setup
  - id: attempt-diamonds
    content: Build AttemptDiamonds.kt — ♦ ♦ ♦ attempt indicator composable with scale+fade animation when a diamond is lost (tween 300ms).
    status: pending
    dependencies:
      - project-setup
  - id: splash-screen
    content: Create SplashScreen with Joker eye opening animation (iris scale 0→1 with glow, spring dampingRatio=0.5), title reveal, dark atmosphere, auto-advance to Home.
    status: pending
    dependencies:
      - project-setup
  - id: home-screen
    content: Build HomeScreen with 7 card grid — staggered float-up (spring 80ms delay each), cursed/broken visual states (pulse glow for cursed, grey+crack for broken), progress counter, difficulty selector.
    status: pending
    dependencies:
      - home-viewmodel
  - id: card-intro-screen
    content: Build CardIntroScreen — full-screen card slide up (spring enter transition), typewriter Joker monologue, "ATTEMPT THE CURSE" button.
    status: pending
    dependencies:
      - joker-monologue
      - home-screen
  - id: card1-sequence
    content: Implement SequenceScreen.kt — 7 symbols in 3+4 grid, sequence show phase with glow pulse (animateColorAsState tween 200ms), player input phase, green/red tap feedback, sequence progress indicator.
    status: pending
    dependencies:
      - puzzle-viewmodel
      - attempt-diamonds
  - id: card2-code-cracker
    content: Implement CodeCrackerScreen.kt — dark riddle text box, 4 number dials (0–9 with +/− buttons), CONFIRM CODE button, 3-strike tracking.
    status: pending
    dependencies:
      - puzzle-viewmodel
      - attempt-diamonds
  - id: card3-pattern-mirror
    content: Implement PatternMirrorScreen.kt — dual grid (Joker's pattern | Player's mirror), lit tile glow (red), player tap to toggle tiles, CONFIRM MIRROR with wrong-tile flash.
    status: pending
    dependencies:
      - puzzle-viewmodel
      - attempt-diamonds
  - id: card4-word-unscramble
    content: Implement WordUnscrambleScreen.kt — scrambled letter tiles with pointerInput detectDragGestures, empty answer slots, tile snap-to-slot logic, CONFIRM WORD with shake+return on wrong.
    status: pending
    dependencies:
      - puzzle-viewmodel
      - attempt-diamonds
  - id: card5-odd-one-out
    content: Implement OddOneOutScreen.kt — symbol grid with one outlier (colour/rotation/size difference), 5 rounds per attempt, round counter, tap-to-select logic.
    status: pending
    dependencies:
      - puzzle-viewmodel
      - attempt-diamonds
  - id: card6-colour-sequence
    content: Implement ColourSequenceScreen.kt — 7 coloured panels in a circle, Simon Says flash sequence (animateColorAsState), growing sequence length, sequence progress indicator.
    status: pending
    dependencies:
      - puzzle-viewmodel
      - attempt-diamonds
  - id: fail-screen
    content: Build FailScreen — Joker taunt with typewriter, attempts remaining shown via AttemptDiamonds, RETRY button back to puzzle.
    status: pending
    dependencies:
      - joker-monologue
      - attempt-diamonds
  - id: reset-screen
    content: Build ResetScreen — full 3-fail Joker reset monologue with typewriter, card wiped message, return to Home (card state cleared in Room).
    status: pending
    dependencies:
      - joker-monologue
      - room-db
  - id: card7-final-boss
    content: Implement FinalBossScreen.kt — 3-step sequential challenge (Step 1: Symbol Sequence len 5, Step 2: Pattern Mirror 3×3 with 5 tiles, Step 3: Colour Sequence start 4). Any step failure returns to Step 1. Full attempt tracking across all 3 steps.
    status: pending
    dependencies:
      - card1-sequence
      - card3-pattern-mirror
      - card6-colour-sequence
      - puzzle-viewmodel
  - id: curse-break-canvas
    content: Build CurseBreakCanvas.kt — Canvas particle burst composable (60 particles radial velocity from card centre), used in CurseBreakScreen cinematic.
    status: pending
    dependencies:
      - project-setup
  - id: curse-break-screen
    content: Implement CurseBreakScreen.kt — full 3-second cinematic sequence: card shake (spring dampingRatio=0.08), crack lines (Canvas tween 300ms), 60-particle burst, accent colour screen flash, "CURSE BROKEN" banner slam (spring stiffness=600), solved card fade, Joker success typewriter quote, auto-return to Home.
    status: pending
    dependencies:
      - curse-break-canvas
      - joker-monologue
  - id: victory-screen
    content: Build VictoryScreen — all 7 symbols burst simultaneously (Canvas multi-particle system), full escape cinematic, triggered only when all 7 curses broken.
    status: pending
    dependencies:
      - curse-break-canvas
      - home-viewmodel
  - id: leaderboard-screen
    content: Create LeaderboardScreen — best completion times from Room CardProgress.bestTimeMs, sorted list display.
    status: pending
    dependencies:
      - room-db
  - id: polish-qa
    content: QA all 7 cards full run, verify 3-strike resets, confirm curse-break animation all 5 stages, test airplane mode, verify persistence after restart, fix any jank on real device.
    status: pending
    dependencies:
      - card7-final-boss
      - curse-break-screen
      - victory-screen
      - leaderboard-screen
  - id: apk-build
    content: Final client APK build — release variant, verify all acceptance criteria, deliver.
    status: pending
    dependencies:
      - polish-qa
---

# Joker's Curse — Escape Room Development Plan

> **Overview**: Build a dark narrative puzzle game where the Joker has cursed 7 gothic cards and imprisoned the player in his carnival. Each card hides a completely different puzzle mechanic — memory sequences, code cracking, pattern mirroring, word unscrambling, odd-one-out, colour sequences, and a 3-step final boss. All 7 cards available from the start. 3 strikes resets any card from scratch. Break all 7 curses to escape. Built entirely in Jetpack Compose using the Jok_Ag3 asset pack. Fully offline. All content in JSON. 3-day delivery sprint.

> **Asset Note**: Agents must extract `/Users/veekshith/Downloads/Jok_Ag3_elements.zip` and copy assets into `app/src/main/res/drawable/`. The zip contains all 7 card symbol PNGs (`1.PNG`–`7.png` → `ag3_sym_1.png`–`ag3_sym_7.png`) and 5 card background JPGs (`back 1.JPG`–`back 5.jpg` → `ag3_back_1.jpg`–`ag3_back_5.jpg`). Do NOT recreate these as vector drawables — the PNGs work directly with `painterResource()` in Compose. The `Jok_Ag3/` folder in the project root contains the promotional/store listing images (portrait art, icon, banner) — use those for splash and launcher only. `Jok_Ag3 2/` contains identical duplicates of the promo images — ignore it.

---

## ✅ Project Status & Todos

### 🏗 Phase A: Foundation (Day 1)
- [x] **A1: Project Setup** <!-- id: project-setup -->
- [x] **A2: Data Models + CurseRepository** <!-- id: data-models -->
- [x] **A3: Room Database** <!-- id: room-db -->
- [ ] **A4: PuzzleViewModel + 3-Strike System** <!-- id: puzzle-viewmodel -->
- [ ] **A5: HomeViewModel** <!-- id: home-viewmodel -->
- [ ] **A6: JokerMonologue Component** <!-- id: joker-monologue -->
- [ ] **A7: AttemptDiamonds Component** <!-- id: attempt-diamonds -->

### 🎭 Phase B: Core Screens (Day 1)
- [ ] **B1: Splash Screen** <!-- id: splash-screen -->
- [ ] **B2: Home Screen** <!-- id: home-screen -->
- [ ] **B3: Card Intro Screen** <!-- id: card-intro-screen -->

### 🃏 Phase C: Puzzle Screens (Day 2)
- [ ] **C1: Card 1 — Symbol Sequence** <!-- id: card1-sequence -->
- [ ] **C2: Card 2 — Code Cracker** <!-- id: card2-code-cracker -->
- [ ] **C3: Card 3 — Pattern Mirror** <!-- id: card3-pattern-mirror -->
- [ ] **C4: Card 4 — Word Unscramble** <!-- id: card4-word-unscramble -->
- [ ] **C5: Card 5 — Odd One Out** <!-- id: card5-odd-one-out -->
- [ ] **C6: Card 6 — Colour Sequence** <!-- id: card6-colour-sequence -->
- [ ] **C7: Fail Screen** <!-- id: fail-screen -->
- [ ] **C8: Reset Screen** <!-- id: reset-screen -->

### 💀 Phase D: Final Boss + Cinematics (Day 3)
- [ ] **D1: Card 7 — Final Boss** <!-- id: card7-final-boss -->
- [ ] **D2: CurseBreakCanvas Particle System** <!-- id: curse-break-canvas -->
- [ ] **D3: Curse Break Cinematic Screen** <!-- id: curse-break-screen -->
- [ ] **D4: Victory Screen** <!-- id: victory-screen -->
- [ ] **D5: Leaderboard Screen** <!-- id: leaderboard-screen -->

### 🚀 Phase E: Delivery (Day 3)
- [ ] **E1: Polish & QA — All 7 Cards Full Run** <!-- id: polish-qa -->
- [ ] **E2: Client APK Build** <!-- id: apk-build -->

---

## 🏗 System Architecture

### 1. High-Level Architecture (MVVM + Compose)
```
┌─────────────────────────────────────────────────────────────────┐
│                     Jetpack Compose UI Layer                    │
│  (Splash, Home, CardIntro, 7 Puzzle Screens, CurseBreak,       │
│   Fail, Reset, Victory, Leaderboard)                           │
├─────────────────────────────────────────────────────────────────┤
│                       ViewModel Layer                           │
│      (HomeViewModel + PuzzleViewModel + CurseBreakViewModel)   │
├─────────────────────────────────────────────────────────────────┤
│                      Content Layer                              │
│      (CurseRepository — Gson parse of assets/curses.json)      │
├─────────────────────────────────────────────────────────────────┤
│                       Data Layer                                │
│      (Room DB: CardProgress entity, DAO, AppDatabase)          │
└─────────────────────────────────────────────────────────────────┘
```

### 2. State Architecture (StateFlow + MVVM)
```
PuzzleViewModel
    ├── PuzzleUiState (StateFlow)
    │   ├── cardId: String
    │   ├── puzzleType: PuzzleType
    │   ├── attemptsRemaining: Int       (0–3)
    │   ├── phase: PuzzlePhase           (SHOWING | INPUT | CHECKING | SUCCESS | FAIL)
    │   ├── isComplete: Boolean
    │   ├── isFailed: Boolean
    │   ├── isReset: Boolean
    │   ├── sequence: List<Int>          (Card 1, 7)
    │   ├── playerInput: List<Int>       (Card 1, 7)
    │   ├── gridPattern: List<Boolean>   (Card 3, 7)
    │   ├── playerGrid: List<Boolean>    (Card 3, 7)
    │   ├── dialValues: List<Int>        (Card 2)
    │   ├── scrambledLetters: List<Char> (Card 4)
    │   ├── playerLetters: List<Char?>   (Card 4)
    │   ├── activeColour: Int?           (Card 6, 7)
    │   └── finalBossStep: Int           (Card 7 only — 1, 2, or 3)
    └── onAttemptFailed(cardId) — 3-strike → full reset

HomeViewModel
    ├── cardStates: Map<String, CardState> (StateFlow)
    ├── brokenCount: StateFlow<Int>
    └── Room DAO (persist broken state + best times)

CurseBreakViewModel
    └── animationPhase: StateFlow<BreakPhase>
        (SHAKE → CRACK → PARTICLES → FLASH → BANNER → FADE → QUOTE → DONE)
```

### 3. 3-Strike System
```kotlin
data class CardState(
    val cardId: String,
    val isCurseBroken: Boolean = false,
    val attemptsRemaining: Int = 3,
    val isLocked: Boolean = false    // true after 3 fails until reset
)

fun onAttemptFailed(cardId: String) {
    val card = cardStates[cardId] ?: return
    val newAttempts = card.attemptsRemaining - 1
    if (newAttempts <= 0) {
        // Full reset — puzzle state wiped, attempts restored to 3
        cardStates[cardId] = card.copy(attemptsRemaining = 3, isLocked = false)
        triggerResetAnimation(cardId)
        showJokerResetQuote(cardId)
    } else {
        cardStates[cardId] = card.copy(attemptsRemaining = newAttempts)
        showJokerFailQuote(cardId)
    }
}
```

### 4. Curse Break Animation Timeline
```
0ms    → Card shakes violently       spring(dampingRatio=0.08, stiffness=800)
400ms  → Crack lines appear          Canvas path draw tween(300ms)
700ms  → Symbol shatters             60-particle burst radial Canvas
900ms  → Screen accent flash         animateColorAsState tween(200ms)
1100ms → "CURSE BROKEN" banner slams spring(stiffness=600) slide from top
1600ms → Card fades to solved state  alpha tween(400ms)
2000ms → Joker success quote         typewriter 25ms/char LaunchedEffect
3000ms → Auto-return to Home         NavController popBackStack
```

### 5. Project File Structure
```
app/src/main/
├── assets/
│   └── curses.json                         # All 7 cards, monologues, puzzle configs
├── java/.../jokersescaperoom/
│   ├── MainActivity.kt                     # NavHost, dark fade transitions
│   ├── ui/
│   │   ├── SplashScreen.kt                 # Joker eye open animation
│   │   ├── HomeScreen.kt                   # 7 card grid, cursed/broken state
│   │   ├── CardIntroScreen.kt              # Full-screen card + typewriter monologue
│   │   ├── FailScreen.kt                   # Joker taunt, retry button
│   │   ├── ResetScreen.kt                  # 3-fail reset monologue
│   │   ├── CurseBreakScreen.kt             # 3-second cinematic sequence
│   │   ├── VictoryScreen.kt                # All 7 broken — escape cinematic
│   │   ├── LeaderboardScreen.kt            # Best times from Room
│   │   ├── puzzle/
│   │   │   ├── SequenceScreen.kt           # Card 1 — symbol sequence
│   │   │   ├── CodeCrackerScreen.kt        # Card 2 — number dials
│   │   │   ├── PatternMirrorScreen.kt      # Card 3 — grid mirror
│   │   │   ├── WordUnscrambleScreen.kt     # Card 4 — drag letters
│   │   │   ├── OddOneOutScreen.kt          # Card 5 — find the odd symbol
│   │   │   ├── ColourSequenceScreen.kt     # Card 6 — Simon Says
│   │   │   └── FinalBossScreen.kt          # Card 7 — 3-step combined
│   │   └── components/
│   │       ├── JokerMonologue.kt           # Reusable typewriter composable
│   │       ├── AttemptDiamonds.kt          # ♦ ♦ ♦ attempt indicator
│   │       └── CurseBreakCanvas.kt         # 60-particle burst Canvas
│   ├── viewmodel/
│   │   ├── HomeViewModel.kt                # Card states, broken count
│   │   ├── PuzzleViewModel.kt              # Active puzzle state + attempt tracking
│   │   └── CurseBreakViewModel.kt          # Break animation state machine
│   └── data/
│       ├── CurseRepository.kt              # Gson parse curses.json
│       ├── model/
│       │   ├── CursedCard.kt               # Data model from JSON
│       │   ├── PuzzleUiState.kt            # Full puzzle state
│       │   ├── PuzzleType.kt               # Enum — 7 puzzle types
│       │   └── PuzzlePhase.kt              # Enum — SHOWING, INPUT, CHECKING, etc.
│       └── db/
│           ├── CardProgress.kt             # Room entity
│           ├── CardProgressDao.kt          # DAO — queries + updates
│           └── AppDatabase.kt             # Room database
└── res/
    └── drawable/
        ├── joker_portrait.png              # Jok_Ag3/1.png — Joker character (splash bg)
        ├── bg_777_gold.png                 # Jok_Ag3/2.png — 777/cherries (card bg)
        ├── bg_slots_fruits.png             # Jok_Ag3/3.png — fruits/bell (card bg)
        ├── ic_launcher.png                 # Jok_Ag3/icon_512.png — app icon
        ├── ic_launcher_negative.png        # Jok_Ag3/negative.png — adaptive icon fg
        ├── ag3_sym_1.png                   # Dark Jester symbol (from Jok_Ag3_elements/1.PNG)
        ├── ag3_sym_2.png                   # Skull Card symbol (from Jok_Ag3_elements/2.PNG)
        ├── ag3_sym_3.png                   # Blood Diamond symbol (from Jok_Ag3_elements/3.PNG)
        ├── ag3_sym_4.png                   # Shadow Spade symbol (from Jok_Ag3_elements/4.png)
        ├── ag3_sym_5.png                   # Cursed Crown symbol (from Jok_Ag3_elements/5.png)
        ├── ag3_sym_6.png                   # Demon Club symbol (from Jok_Ag3_elements/6.png)
        ├── ag3_sym_7.png                   # Joker's Eye symbol (from Jok_Ag3_elements/7.png)
        ├── ag3_back_1.jpg                  # Card background 1 (from Jok_Ag3_elements/back 1.JPG)
        ├── ag3_back_2.jpg                  # Card background 2 (from Jok_Ag3_elements/back 2.JPG)
        ├── ag3_back_3.jpg                  # Card background 3 (from Jok_Ag3_elements/back 3.JPG)
        ├── ag3_back_4.jpg                  # Card background 4 (from Jok_Ag3_elements/back 4.jpg)
        └── ag3_back_5.jpg                  # Card background 5 (from Jok_Ag3_elements/back 5.jpg)
```

### 6. Asset Mapping Reference

| Source File | Drawable Name | Usage | Notes |
|-------------|---------------|-------|-------|
| `Jok_Ag3/1.png` | `joker_portrait.png` | Splash screen background, card intro backdrop | Full Joker character portrait — fiery, dark |
| `Jok_Ag3/2.png` | `bg_777_gold.png` | Card background variant (golden) | 777/cherries slot art |
| `Jok_Ag3/3.png` | `bg_slots_fruits.png` | Card background variant (deep red) | Fruits/bell slot art |
| `Jok_Ag3/4.png` | Store listing only | Play Store screenshot — not used in-app | Phone mockup promotional image |
| `Jok_Ag3/banner_1024x500.png` | Store listing banner | Play Store feature graphic | Joker + slot machine wide banner |
| `Jok_Ag3/icon_512.png` | `ic_launcher.png` | App launcher icon | Joker face square icon |
| `Jok_Ag3/negative.png` | `ic_launcher_negative.png` | Adaptive icon foreground layer | White/negative icon variant |
| `Jok_Ag3 2/` | Duplicates | Same files as `Jok_Ag3/` — use primary folder | Identical copies |
| `Jok_Ag3_elements/1.PNG`–`7.png` | `ag3_sym_1.png`–`ag3_sym_7.png` | 7 card symbols — puzzle grids, home grid | Copy directly as PNG — no vector conversion needed |
| `Jok_Ag3_elements/back 1.JPG`–`back 5.jpg` | `ag3_back_1.jpg`–`ag3_back_5.jpg` | 5 card face backgrounds per curses.json | All 5 required — cards share backgrounds per JSON config |

---

## 🚀 Detailed Implementation Roadmap

---

## Phase A: Foundation (Day 1)

### A1: Project Setup <!-- id: project-setup -->
> **Goal**: Configure Compose + Room + Navigation + Gson. Copy Jok_Ag3 assets. Create curses.json. Wire NavHost with dark fade transitions.

**Duration**: 1.5 Hours

**Files to create/modify:**
| File | Description |
|------|-------------|
| `build.gradle.kts` (app) | Add Compose, Room, Navigation, Gson, Lifecycle, KSP |
| `gradle/libs.versions.toml` | Version catalog |
| `assets/curses.json` | Full 7-card content JSON (from PRD spec) |
| `res/drawable/joker_portrait.png` | Copy from `Jok_Ag3/1.png` |
| `res/drawable/bg_777_gold.png` | Copy from `Jok_Ag3/2.png` |
| `res/drawable/bg_slots_fruits.png` | Copy from `Jok_Ag3/3.png` |
| `res/drawable/ic_launcher.png` | Copy from `Jok_Ag3/icon_512.png` |
| `res/drawable/ic_launcher_negative.png` | Copy from `Jok_Ag3/negative.png` |
| `MainActivity.kt` | NavHost with `fadeIn + fadeOut tween(350ms)` transitions |

**Key Dependencies:**
```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.animation:animation")
implementation("androidx.activity:activity-compose:1.8.2")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Gson
implementation("com.google.code.gson:gson:2.10.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

**Asset Copy Commands:**
```bash
# Extract elements zip first
cd /Users/veekshith/Downloads && unzip -o Jok_Ag3_elements.zip

# Card symbols (7 PNGs — use directly, no vector conversion)
cp "Jok_Ag3_elements/1.PNG"     app/src/main/res/drawable/ag3_sym_1.png
cp "Jok_Ag3_elements/2.PNG"     app/src/main/res/drawable/ag3_sym_2.png
cp "Jok_Ag3_elements/3.PNG"     app/src/main/res/drawable/ag3_sym_3.png
cp "Jok_Ag3_elements/4.png"     app/src/main/res/drawable/ag3_sym_4.png
cp "Jok_Ag3_elements/5.png"     app/src/main/res/drawable/ag3_sym_5.png
cp "Jok_Ag3_elements/6.png"     app/src/main/res/drawable/ag3_sym_6.png
cp "Jok_Ag3_elements/7.png"     app/src/main/res/drawable/ag3_sym_7.png

# Card backgrounds (all 5 required)
cp "Jok_Ag3_elements/back 1.JPG"  app/src/main/res/drawable/ag3_back_1.jpg
cp "Jok_Ag3_elements/back 2.JPG"  app/src/main/res/drawable/ag3_back_2.jpg
cp "Jok_Ag3_elements/back 3.JPG"  app/src/main/res/drawable/ag3_back_3.jpg
cp "Jok_Ag3_elements/back 4.jpg"  app/src/main/res/drawable/ag3_back_4.jpg
cp "Jok_Ag3_elements/back 5.jpg"  app/src/main/res/drawable/ag3_back_5.jpg

# Promo / splash / launcher (from Jok_Ag3 folder in project root)
cp "Jok_Ag3/1.png"          app/src/main/res/drawable/joker_portrait.png
cp "Jok_Ag3/icon_512.png"   app/src/main/res/drawable/ic_launcher.png
cp "Jok_Ag3/negative.png"   app/src/main/res/drawable/ic_launcher_negative.png
```

---

### A2: Data Models + CurseRepository <!-- id: data-models -->
> **Goal**: All data models, enums, and Gson parse of curses.json.

**Duration**: 1 Hour

**Files to create:**
| File | Key Content |
|------|-------------|
| `data/model/CursedCard.kt` | Data class matching curses.json structure (id, title, symbol, background, accentColor, puzzleType, intro, successQuote, failQuote, resetQuote, difficulty config) |
| `data/model/PuzzleUiState.kt` | Full state data class — all card-specific fields combined |
| `data/model/PuzzleType.kt` | Enum: SYMBOL_SEQUENCE, CODE_CRACKER, PATTERN_MIRROR, WORD_UNSCRAMBLE, ODD_ONE_OUT, COLOUR_SEQUENCE, FINAL_BOSS |
| `data/model/PuzzlePhase.kt` | Enum: SHOWING, INPUT, CHECKING, SUCCESS, FAIL |
| `data/CurseRepository.kt` | `fun getCurses(context): List<CursedCard>` — open assets/curses.json, Gson parse |

**CurseRepository pattern:**
```kotlin
class CurseRepository(private val context: Context) {
    fun getCurses(): List<CursedCard> {
        val json = context.assets.open("curses.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(json, CursesResponse::class.java).cards
    }
}
```

---

### A3: Room Database <!-- id: room-db -->
> **Goal**: Persist curse broken status, best times, attempt/reset counters.

**Duration**: 45 Minutes

**Files to create:**
| File | Key Content |
|------|-------------|
| `data/db/CardProgress.kt` | `@Entity` — cardId (PK), isCurseBroken, bestTimeMs, totalAttempts, totalResets, brokenAt |
| `data/db/CardProgressDao.kt` | `@Dao` — getAll(), getById(id), upsert(progress), markBroken(id, timeMs), incrementAttempts(id), incrementResets(id) |
| `data/db/AppDatabase.kt` | `@Database(entities=[CardProgress::class], version=1)` |

---

### A4: PuzzleViewModel + 3-Strike System <!-- id: puzzle-viewmodel -->
> **Goal**: Full puzzle state machine with attempt tracking, reset logic, and StateFlow for all 7 puzzle types.

**Duration**: 1.5 Hours

**Key logic to implement:**
- `loadPuzzle(cardId)` — fetch card from CurseRepository, init PuzzleUiState
- `onAttemptFailed(cardId)` — decrement attempts; at 0: full puzzle reset + triggerResetQuote
- `onPuzzleSolved(cardId)` — mark complete, save bestTime to Room, emit SUCCESS phase
- `submitSequence()` — Card 1/7: compare playerInput vs sequence
- `submitCode()` — Card 2: join dialValues == answer string
- `submitMirror()` — Card 3/7: compare playerGrid vs gridPattern
- `submitWord()` — Card 4: join playerLetters == answer
- `tapOddOne(index)` — Card 5: check if tapped index is the odd symbol
- `tapColour(colourIndex)` — Card 6/7: validate against growing sequence

---

### A5: HomeViewModel <!-- id: home-viewmodel -->
> **Goal**: Load all 7 CardState from Room, expose card grid, track broken count.

**Duration**: 30 Minutes

**Key logic:**
- On init: load all 7 CardProgress from Room, map to CardState
- `brokenCount: StateFlow<Int>` — count of cards where isCurseBroken = true
- `isVictory: StateFlow<Boolean>` — brokenCount == 7 → navigate to Victory
- Observe Room for real-time updates after each puzzle completion

---

### A6: JokerMonologue Component <!-- id: joker-monologue -->
> **Goal**: Reusable typewriter composable used on every Joker quote screen.

**Duration**: 30 Minutes

```kotlin
@Composable
fun JokerMonologue(text: String, onComplete: () -> Unit = {}) {
    var displayed by remember { mutableStateOf("") }
    LaunchedEffect(text) {
        text.forEachIndexed { i, _ ->
            delay(25L)
            displayed = text.substring(0, i + 1)
        }
        onComplete()
    }
    Text(displayed, style = jokerTextStyle, color = Color.White)
}
```

---

### A7: AttemptDiamonds Component <!-- id: attempt-diamonds -->
> **Goal**: ♦ ♦ ♦ attempt indicator with scale+fade animation on loss.

**Duration**: 30 Minutes

- Three diamond icons rendered from `attemptsRemaining` count
- Lost diamond: `animateFloatAsState` scale 1→0 + alpha 1→0 via `tween(300ms)`
- Active diamonds: solid accent colour; empty: dark grey outline

---

## Phase B: Core Screens (Day 1)

### B1: Splash Screen <!-- id: splash-screen -->
> **Goal**: Joker eye opens, title reveals, dark atmosphere, auto-advance to Home.

**Duration**: 1 Hour

**Animation sequence:**
```
0ms    → Black screen
300ms  → Eye iris scales 0→1 (spring dampingRatio=0.5) with glow halo
800ms  → "JOKER'S CURSE" title fades in (tween 600ms)
1500ms → Subtitle "Can you escape the carnival?" typewriter
2500ms → Fade to Home (dark fade tween 350ms)
```

**Key composables:** `Canvas` iris draw, `animateFloatAsState` for scale, `infiniteTransition` for glow pulse.

---

### B2: Home Screen <!-- id: home-screen -->
> **Goal**: 7 card grid with staggered entrance, visual cursed/broken states, progress tracker.

**Duration**: 1.5 Hours

**Layout:**
```
[ "7 Curses Await"                           ]
[ Progress: 2 / 7 broken              [⏱]   ]
[ ─────────────────────────────────────────  ]
[  Card 1  | Card 2  | Card 3               ]
[  Card 4  | Card 5  | Card 6               ]
[              Card 7                       ]
[ ─────────────────────────────────────────  ]
[ [ LEADERBOARD ]                            ]
```

**Card visual states:**
- **Cursed**: slow pulse glow `infiniteTransition` alpha 0.7↔1.0, accent colour border
- **Broken**: grey desaturated + crack overlay, `ColorFilter.colorMatrix` greyscale

**Stagger entrance:** `LaunchedEffect` with index × 80ms delay, `spring` offset Y 100→0

---

### B3: Card Intro Screen <!-- id: card-intro-screen -->
> **Goal**: Full-screen card reveal with Joker monologue before each puzzle.

**Duration**: 45 Minutes

**Animation:** Card slides up from bottom — `spring` enter transition on NavHost route.

**Layout:**
```
[ Card symbol (full bleed, accent glow)     ]
[ Card title + subtitle                     ]
[ ─────────────────────────────────────────  ]
[ Joker monologue — typewriter text         ]
[ ─────────────────────────────────────────  ]
[ [ ATTEMPT THE CURSE ]  button             ]
```

Uses `JokerMonologue` component. Button enabled only after monologue completes.

---

## Phase C: Puzzle Screens (Day 2)

### C1: Card 1 — Symbol Sequence <!-- id: card1-sequence -->
> **Goal**: Show sequence of symbol flashes, player must replay in order.

**Duration**: 1.5 Hours

**Layout:**
```
[ "Watch carefully..." — Joker monologue    ]
[ ─────────────────────────────────────────  ]
[   7 symbols in 3+4 grid                  ]
[   Active symbol: glow pulse (200ms)      ]
[ ─────────────────────────────────────────  ]
[ Sequence: ● ● ○ ○  (2 of 4)              ]
[ ♦ ♦ ♦ AttemptDiamonds                    ]
```

**Phase logic:**
- `SHOWING`: each symbol in sequence lights up for `displayTimeMs` then goes dark
- `INPUT`: player taps symbols; correct → green spring pulse; wrong → red flash + attempt--
- Sequence indicator dots show progress through player input

---

### C2: Card 2 — Code Cracker <!-- id: card2-code-cracker -->
> **Goal**: Dark riddle → player sets 4 dials → CONFIRM.

**Duration**: 1 Hour

**Layout:**
```
[ Riddle text — dark flavour text box       ]
[ ─────────────────────────────────────────  ]
[  [▲] [▲] [▲] [▲]                         ]
[  [ 3][ 7][ 2][ 1]   ← dials (0–9)        ]
[  [▼] [▼] [▼] [▼]                         ]
[ ─────────────────────────────────────────  ]
[ [ CONFIRM CODE ]                           ]
[ ♦ ♦ ♦ AttemptDiamonds                    ]
```

**Dial logic:** `+` increments 0→9→0 wrap; `−` decrements. `checkCode` joins dial list and compares to JSON answer string.

---

### C3: Card 3 — Pattern Mirror <!-- id: card3-pattern-mirror -->
> **Goal**: Mirror the Joker's lit tile pattern on an opposite grid.

**Duration**: 1.5 Hours

**Layout:**
```
[ "Mirror my madness..."                    ]
[ ─────────────────────────────────────────  ]
[  JOKER'S PATTERN  |  YOUR MIRROR          ]
[  ■ □ □            |  □ □ □               ]
[  □ ■ □            |  □ □ □               ]
[  □ □ ■            |  □ □ □               ]
[ ─────────────────────────────────────────  ]
[ [ CONFIRM MIRROR ]                         ]
[ ♦ ♦ ♦ AttemptDiamonds                    ]
```

**Grid tile:** `Box` with `clickable`, lit = accent glow (`animateColorAsState`), unlit = dark. On wrong confirm: incorrect tiles flash red, correct tiles stay lit.

---

### C4: Card 4 — Word Unscramble <!-- id: card4-word-unscramble -->
> **Goal**: Drag scrambled letter tiles into answer slots.
>
> ⚠️ **Build this first on Day 2.** This is the most complex UI in the project — `detectDragGestures` + absolute offset tracking + slot proximity detection is genuinely tricky. Do not leave it for last when you're tired.

**Duration**: 2 Hours

**Layout:**
```
[ Clue: "What the Joker has placed on you" ]
[ ─────────────────────────────────────────  ]
[  Scrambled: [R][S][E][C][U]              ]
[ ─────────────────────────────────────────  ]
[  Answer:    [_][_][_][_][_]              ]
[ ─────────────────────────────────────────  ]
[ [ CONFIRM WORD ]                           ]
```

**Drag logic:** `pointerInput` + `detectDragGestures`, absolute offset tracking, snap-to-nearest-slot on release. Wrong confirm: tiles `spring(dampingRatio=0.2)` shake animation + return to scrambled row.

---

### C5: Card 5 — Odd One Out <!-- id: card5-odd-one-out -->
> **Goal**: Find the symbol that doesn't belong. 5 rounds per attempt.

**Duration**: 1.5 Hours

**Layout:**
```
[ "One thing is always wrong..."            ]
[ Round 2 of 5                              ]
[ ─────────────────────────────────────────  ]
[  [sym][sym][sym]                          ]
[  [sym][ODD][sym]                          ]
[  [sym][sym][sym]                          ]
[ ─────────────────────────────────────────  ]
[ ♦ ♦ ♦ AttemptDiamonds                    ]
```

**Difference types:** COLOUR (tint variation), ROTATION (`graphicsLayer { rotationZ }`), SUBTLE_SIZE (scale ±15%). Wrong tap → red flash on tapped tile, attempt--.

---

### C6: Card 6 — Colour Sequence <!-- id: card6-colour-sequence -->
> **Goal**: Simon Says with 7 accent-coloured panels. Sequence grows by 1 each round.

**Duration**: 1.5 Hours

**Layout:**
```
[ "The demon demands order..."              ]
[ ─────────────────────────────────────────  ]
[  7 coloured panels arranged in a circle  ]
[  Active panel flashes during show        ]
[ ─────────────────────────────────────────  ]
[ Sequence: ● ● ● ○ ○  (3 of 5)            ]
[ ♦ ♦ ♦ AttemptDiamonds                    ]
```

**Flash logic:** `animateColorAsState` — active: full saturation + brightness boost; idle: 40% alpha. Wrong tap → sequence resets to beginning of that attempt (not full card reset unless 3 full attempts fail).

---

### C7: Fail Screen <!-- id: fail-screen -->
> **Goal**: Joker taunts the player, shows remaining attempts, retry.

**Duration**: 30 Minutes

- `JokerMonologue` typewriter with `failQuote` from card's JSON
- `AttemptDiamonds` showing remaining attempts
- `[ RETRY ]` button → back to puzzle (NavController popBackStack)

---

### C8: Reset Screen <!-- id: reset-screen -->
> **Goal**: All 3 attempts failed — Joker full reset monologue, card wiped.

**Duration**: 30 Minutes

- `JokerMonologue` typewriter with `resetQuote` from card's JSON
- Red/dark dramatic styling to signal failure severity
- `[ RETURN TO CARNIVAL ]` → Home, card state wiped in Room (attempts reset to 3, puzzle state cleared)

---

## Phase D: Final Boss + Cinematics (Day 3)

### D1: Card 7 — Final Boss <!-- id: card7-final-boss -->
> **Goal**: 3-step sequential challenge. Any step failure → back to Step 1.
>
> ⚠️ **Cards 1, 3, and 6 must be fully working and tested before touching this — not just scaffolded.** Card 7 embeds their puzzle logic directly. A bug in Card 6's colour sequence will surface inside the Final Boss in a much harder-to-debug context.

**Duration**: 2 Hours

**Step flow:**
```
Step 1: Symbol Sequence (length 5)
   ↓ pass
Step 2: Pattern Mirror (3×3, 5 lit tiles)
   ↓ pass
Step 3: Colour Sequence (start length 4)
   ↓ pass
→ CURSE BROKEN

Any failure at any step → back to Step 1
3 full attempt failures (completing Step 3 fail) → card full reset
```

**Implementation:** `FinalBossScreen` uses `finalBossStep` from `PuzzleUiState` to switch between sub-composables. Shared step indicator at top: `Step 1 → Step 2 → Step 3`.

---

### D2: CurseBreakCanvas Particle System <!-- id: curse-break-canvas -->
> **Goal**: 60-particle burst from card centre. Reusable Canvas composable.

**Duration**: 1 Hour

```kotlin
@Composable
fun CurseBreakCanvas(accentColor: Color, trigger: Boolean) {
    // 60 particles with random radial velocity
    // Each particle: position, velocity (radialAngle, speed), alpha, size
    // Canvas draw on each Animatable frame
    // Particles fade out over 800ms
}
```

Particles burst from canvas centre at random angles (0–360°), random speeds (200–600dp/s), fade alpha 1→0 over 800ms.

---

### D3: Curse Break Cinematic Screen <!-- id: curse-break-screen -->
> **Goal**: 3-second emotional payoff animation when a curse is broken.

**Duration**: 1.5 Hours

**Full sequence (see Architecture section for timeline):**
- Driven by `CurseBreakViewModel` state machine with phases
- Each phase transitions automatically via `LaunchedEffect` + `delay()`
- `CurseBreakCanvas` particle burst overlaid on card face
- `JokerMonologue` for success quote at 2000ms mark
- `NavController.navigate("home")` at 3000ms

---

### D4: Victory Screen <!-- id: victory-screen -->
> **Goal**: All 7 curses broken — full escape cinematic.

**Duration**: 1 Hour

- Triggered when `HomeViewModel.brokenCount == 7`
- All 7 symbols burst simultaneously with `CurseBreakCanvas` × 7
- "YOU ESCAPED THE CARNIVAL" title with dramatic reveal
- Joker final farewell quote: *"Leave my carnival. But know this — the Joker never forgets a face."*
- Final screen — no auto-navigate, player can view Leaderboard

---

### D5: Leaderboard Screen <!-- id: leaderboard-screen -->
> **Goal**: Show best completion times per card from Room.

**Duration**: 30 Minutes

- Query `CardProgress` ordered by `bestTimeMs ASC`
- Display card name, broken timestamp, best time
- Dark gothic styling matching rest of app

---

## Phase E: Delivery (Day 3)

### E1: Polish & QA <!-- id: polish-qa -->
> **Goal**: Verify all 7 cards function correctly end-to-end. Confirm acceptance criteria.

**Duration**: 2 Hours

**Checklist:**
- [ ] All 7 cards load correct content from curses.json
- [ ] Each puzzle type functions correctly end-to-end
- [ ] 3 failed attempts resets the card completely — puzzle state wiped
- [ ] Correct solution triggers curse-break animation sequence
- [ ] Curse-break animation plays all 5 stages in correct order
- [ ] Broken cards persist after app restart
- [ ] Card 7 Final Boss requires all 3 steps in sequence
- [ ] Card 7 failure on any step returns to Step 1
- [ ] Victory screen triggers only when all 7 curses broken
- [ ] Typewriter monologue plays correctly on all Joker quote screens
- [ ] All animations play without jank on real device
- [ ] App works fully in airplane mode
- [ ] No crashes across full completion of all 7 cards

---

### E2: APK Build <!-- id: apk-build -->
> **Goal**: Release variant APK for client delivery.

**Duration**: 30 Minutes

```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

---

## 📋 Animation Reference

| Element | Animation | Spec |
|---------|-----------|------|
| Splash eye open | Iris scale 0→1 with glow | `spring(dampingRatio=0.5)` |
| Home cards entrance | Stagger float up | `spring` 80ms delay each card |
| Broken card | Grey desaturated + crack overlay | Static visual state |
| Cursed card | Slow pulse glow | `infiniteTransition` alpha 0.7↔1.0 |
| Card intro | Full screen slide up | `spring` enter transition |
| Monologue text | Typewriter letter by letter | 25ms per char `LaunchedEffect` |
| Puzzle symbols flash | Glow during sequence show | `animateColorAsState tween(200ms)` |
| Correct tap | Green pulse scale | `spring(stiffness=500)` |
| Wrong tap | Red flash + shake | `spring(dampingRatio=0.2)` |
| Attempt diamond lost | Scale 1→0 + fade | `tween(300ms)` |
| Curse break — shake | Violent rapid oscillation | `spring(dampingRatio=0.08, stiffness=800)` |
| Curse break — particles | 60-particle burst from centre | Canvas draw, radial velocity |
| Curse break — flash | Accent colour screen wash | `animateColorAsState tween(200ms)` |
| Curse break — banner | Slam down from top | `spring(stiffness=600)` |
| Screen transitions | Dark fade — not slide | `fadeIn + fadeOut tween(350ms)` |

---

## 🗓 Day-by-Day Delivery

| Day | Deliverables |
|-----|-------------|
| **Day 1** | curses.json complete, CurseRepository Gson parse, all data models + enums, PuzzleViewModel with attempt system, Room DB setup, NavHost dark fade transitions, HomeScreen 7 card grid, CardIntroScreen with typewriter monologue, Cards 1+2 puzzle screens fully working |
| **Day 2** | Cards 3+4+5+6 puzzle screens, FailScreen, ResetScreen, attempt diamond component, correct/wrong tap animations, card state persistence in Room |
| **Day 3** | Card 7 Final Boss 3-step logic, CurseBreakScreen full cinematic animation, VictoryScreen, SplashScreen eye animation, LeaderboardScreen, all remaining animations, QA all 7 cards full run, client APK build |
