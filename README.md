# 🎮 Magic Plugin - פלאגין קסמים מתקדם למיינקראפט

פלאגין קסמים מתקדם למיינקראפט עם מערכת מטות, קסמים, וקסמי בנייה לגרסה 1.21.4

## ✨ תכונות עיקריות

### 🌟 מערכת מטות מתקדמת
- **יצירת מטות** - מטות ריקות או מתבניות מוכנות
- **ניהול קסמים** - הוספה, הסרה, ומילוי קסמים
- **תכונות מתקדמות** - הגדרת מאפיינים וקידום
- **שילוב מטות** - שילוב מטות על סדן
- **ניהול חומרים** - הוספה והסרה של חומרים

### 💫 מערכת קסמים מתקדמת
- **קסמי בנייה** - בנייה מהירה וקסומה
- **קסמי פיצוץ** - פיצוצים עם פרמטרים מותאמים
- **קסמי הגנה** - הגנה מפני נזקים
- **קסמי ריפוי** - ריפוי ובופים
- **קסמי תעופה** - תעופה וטלפורט

### 🎯 פקודות מתקדמות
- **/wand** - ניהול מטות מלא
- **/wandp** - ניהול מטות של שחקנים אחרים
- **/magic** - פקודות ניהול
- **/cast** - הפעלת קסמים עם פרמטרים
- **/spells** - רשימת קסמים מפורטת
- **/mgive** - מתן פריטי קסם

## 🚀 התקנה

### דרישות מקדימות
- **Java 17+** מותקן במחשב
- **Minecraft Server 1.21.4** (Spigot/Paper)
- **Bukkit/Spigot API** מותקן

### שלבי התקנה

1. **הורד את הפלאגין**
   ```bash
   git clone https://github.com/magicdeveloper/magic-plugin.git
   cd magic-plugin
   ```

2. **בנה את הפלאגין**
   ```bash
   mvn clean package
   ```

3. **העתק את הקובץ JAR**
   - העתק את הקובץ `target/magic-plugin-1.0.0.jar` לתיקיית `plugins` של השרת

4. **הפעל את השרת**
   - הפלאגין יטען אוטומטית עם השרת

## 💬 פקודות מפורטות

### 🏹 פקודת /wand

#### יצירת מטות
- `/wand` - יצירת מטה ריקה
- `/wand <name>` - יצירת מטה מתבנית מוכנה
- `/wand list` - רשימת תבניות זמינות

#### ניהול קסמים
- `/wand add <spell>` - הוספת קסם למטה
- `/wand remove <spell>` - הסרת קסם מהמטה
- `/wand fill` - מילוי המטה בכל הקסמים הזמינים

#### הגדרות מתקדמות
- `/wand configure <property> <value>` - הגדרת מאפיין
- `/wand upgrade <property> <value>` - קידום מאפיין
- `/wand combine <wand>` - שילוב מטות
- `/wand name <name>` - מתן שם למטה
- `/wand describe` - הצגת מאפייני המטה

#### ניהול חומרים
- `/wand add material <material>` - הוספת חומר
- `/wand remove material <material>` - הסרת חומר

#### קסמים מיוחדים
- `/wand enchant` - יצירת מטה מהפריט המוחזק
- `/wand unenchant` - השמדת המטה
- `/wand enchant <xp>` - הוספת רמות XP למטה

### 👥 פקודת /wandp

#### ניהול מטות של שחקנים אחרים
- `/wandp <player> add <spell>` - הוספת קסם למטה של שחקן
- `/wandp <player> remove <spell>` - הסרת קסם ממטה של שחקן
- `/wandp <player> name <name>` - מתן שם למטה של שחקן
- `/wandp <player> list` - רשימת מטות של שחקן
- `/wandp <player> fill` - מילוי מטה של שחקן
- `/wandp <player> configure <property> <value>` - הגדרת מאפיין
- `/wandp <player> add material <material>` - הוספת חומר
- `/wandp <player> remove material <material>` - הסרת חומר

### 🔮 פקודת /magic

#### ניהול מערכת
- `/magic load` - טעינה מחדש של קבצי תצורה
- `/magic save` - שמירת נתוני שחקנים
- `/magic commit` - אישור כל השינויים
- `/magic cancel` - ביטול גלי בנייה

#### רשימות וניקוי
- `/magic list <type> [player]` - רשימת פריטים (wands, automata, maps, tasks)
- `/magic clean <player>` - ניקוי מטות אבודות של שחקן
- `/magic clean ALL` - ניקוי כל המטות האבודות

### ⚡ פקודת /cast

#### הפעלת קסמים עם פרמטרים
- `/cast <spell>` - הפעלת קסם רגיל
- `/cast <spell> <param> <value>` - הפעלת קסם עם פרמטרים

#### דוגמאות שימוש
- `/cast boom size 20 fire true` - פיצוץ גדול עם אש
- `/cast arrow fire true count 50` - 50 חיצי אש
- `/cast familiar type chicken count 30` - 30 תרנגולות קסומות
- `/cast paint material gold_block` - צביעה בבלוקי זהב
- `/cast recurse material water` - יצירת מים חוזרים

### 📚 פקודת /spells

#### רשימת קסמים
- `/spells` - רשימת כל הקסמים הזמינים
- `/spells category <category>` - קסמים בקטגוריה מסוימת
- `/spells search <query>` - חיפוש קסמים
- `/spells info <spell>` - מידע מפורט על קסם
- `/spells help` - עזרה

### 🎁 פקודת /mgive

#### מתן פריטי קסם
- `/mgive <item>` - מתן פריט לעצמך
- `/mgive <item> <amount>` - מתן כמות פריטים לעצמך
- `/mgive <player> <item>` - מתן פריט לשחקן
- `/mgive <player> <item> <amount>` - מתן כמות פריטים לשחקן

#### דוגמאות שימוש
- `/mgive elder` - מתן מטה זקן
- `/mgive PlayerName fireball 5` - מתן 5 קסמי כדור אש
- `/mgive PlayerName xp 200` - מתן 200 XP
- `/mgive spell:wolf` - מתן קסם זאב
- `/mgive book:engineering` - מתן ספר קסמי הנדסה

## 🏗️ בנייה מהמקור

### דרישות פיתוח
- Java 17+
- Maven 3.6+
- IDE עם תמיכה ב-Java (IntelliJ IDEA מומלץ)

### פקודות בנייה
```bash
# בנייה רגילה
mvn clean package

# בנייה עם בדיקות
mvn clean test package

# בנייה ללא בדיקות
mvn clean package -DskipTests
```

## 📁 מבנה הפרויקט

```
magic-plugin/
├── src/
│   ├── main/
│   │   ├── java/com/magicplugin/
│   │   │   ├── MagicPlugin.java           # הקובץ הראשי של הפלאגין
│   │   │   ├── commands/                   # פקודות
│   │   │   │   ├── WandCommand.java        # פקודת /wand
│   │   │   │   ├── WandPlayerCommand.java  # פקודת /wandp
│   │   │   │   ├── MagicCommand.java       # פקודת /magic
│   │   │   │   ├── CastCommand.java        # פקודת /cast
│   │   │   │   ├── SpellsCommand.java      # פקודת /spells
│   │   │   │   └── MagicGiveCommand.java   # פקודת /mgive
│   │   │   ├── managers/                   # מנהלים
│   │   │   ├── objects/                    # אובייקטים
│   │   │   └── listeners/                  # מאזינים
│   │   └── resources/
│   │       ├── plugin.yml                  # הגדרות הפלאגין
│   │       ├── config.yml                  # קובץ תצורה
│   │       ├── spells.yml                  # הגדרות קסמים
│   │       └── wands.yml                   # הגדרות מטות
├── pom.xml                                  # הגדרות Maven
└── README.md                                # קובץ זה
```

## 🔧 תצורה

### קבצי תצורה
- **config.yml** - הגדרות כלליות של הפלאגין
- **spells.yml** - הגדרות קסמים ופרמטרים
- **wands.yml** - תבניות מטות מוכנות
- **messages.yml** - הודעות וטקסטים

### הגדרות אפשריות
- שינוי עלויות קסמים
- התאמת זמני המתנה
- הגדרת הרשאות
- שינוי התנהגות קסמים
- התאמת הודעות

## 🎮 שימוש במשחק

### יצירת מטה ראשונה
1. **השתמש בפקודה:** `/wand`
2. **קבל מטה ריקה** עם שם "Magic Wand"
3. **הוסף קסמים:** `/wand add fireball`
4. **צבע את המטה:** `/wand name "מטה שלי"`

### הפעלת קסמים
1. **החזק מטה** ביד ימין
2. **לחץ שמאלי** להפעלת הקסם הפעיל
3. **לחץ ימני** לפתיחת מלאי המטה
4. **שנה קסמים** באמצעות המלאי

### קסמי בנייה
- **/cast pillar** - יצירת עמוד
- **/cast bridge** - יצירת גשר
- **/cast wall** - יצירת קיר
- **/cast fill** - מילוי אזור
- **/cast undo** - ביטול פעולה אחרונה

## 🐛 פתרון בעיות

### בעיות נפוצות
1. **הפלאגין לא נטען**
   - ודא שיש לך Java 17+
   - בדוק תאימות גרסאות
   - בדוק את לוגי השרת

2. **קסמים לא עובדים**
   - ודא שיש לך הרשאות מתאימות
   - בדוק שהשחקן מחזיק מטה
   - בדוק עלויות וזמני המתנה

3. **שגיאות הרשאות**
   - בדוק הגדרות הרשאות
   - ודא שהשחקן בקבוצה הנכונה
   - בדוק קובץ plugin.yml

## 🤝 תרומה

אנחנו תמיד שמחים לקבל תרומות! אם יש לך רעיונות לשיפורים:

1. Fork את הפרויקט
2. צור branch חדש
3. בצע את השינויים
4. שלח Pull Request

## 📄 רישיון

הפרויקט מוגן תחת רישיון MIT. ראה קובץ `LICENSE` לפרטים.

## 🙏 תודות

- צוות Bukkit/Spigot
- קהילת Minecraft Plugin Development
- כל התורמים לפרויקט

## 📞 תמיכה

אם אתה נתקל בבעיות או יש לך שאלות:

- פתח Issue ב-GitHub
- צור דיון ב-Discussions
- פנה אלינו בדוא"ל

---

**Magic Plugin** - פלאגין קסמים מתקדם וחכם למיינקראפט! ✨🎮🔮 