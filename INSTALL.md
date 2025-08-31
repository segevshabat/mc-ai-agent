# 🚀 הוראות התקנה מהירה - Magic AI Player

## 📋 דרישות מקדימות
- **Java 17+** מותקן במחשב
- **Minecraft Server 1.21.4** פועל
- **Fabric Loader 0.15.0+** מותקן
- **Fabric API** מותקן

## ⚡ התקנה מהירה (5 דקות)

### 1. הורדה ובנייה
```bash
# הורד את הפרויקט
git clone https://github.com/magicai/minecraft-magic-ai.git
cd minecraft-magic-ai

# בנה עם Maven
mvn clean package

# או בנה עם Gradle
./gradlew build
```

### 2. התקנה בשרת
```bash
# העתק את הקובץ JAR לתיקיית mods
cp target/minecraft-magic-ai-1.0.0.jar /path/to/minecraft/mods/

# או עם Gradle
cp build/libs/minecraft-magic-ai-1.0.0.jar /path/to/minecraft/mods/
```

### 3. הפעלה
```bash
# הפעל את השרת
java -jar fabric-server-launch.jar

# המוד יטען אוטומטית!
```

## 🎯 בדיקת התקנה

כשהשרת עולה, תראה:
```
[INFO] Initializing Magic AI Mod for Minecraft 1.21.4
[INFO] Magic AI Mod initialized successfully!
[INFO] Magic AI connected to server: [IP]
```

## 💬 בדיקת פונקציונליות

1. **התחבר לשרת**
2. **כתוב בצ'אט:** `שלום`
3. **תקבל תשובה:** `✨ שלום [שמך]! איך אני יכול לעזור לך היום? ✨`

## 🔧 פתרון בעיות נפוצות

### המוד לא נטען?
```bash
# בדוק גרסת Java
java -version

# בדוק לוגי השרת
tail -f logs/latest.log
```

### שגיאות Mixin?
```bash
# ודא שיש לך Fabric API
ls mods/ | grep fabric-api

# בדוק תאימות גרסאות
cat mods/fabric-api-*.jar | grep "minecraft"
```

### קסמים לא עובדים?
- ודא שיש לך הרשאות מתאימות
- בדוק שהשחקן נמצא בעולם
- נסה לכתוב `עזרה` בצ'אט

## 📱 פקודות מהירות

| פקודה | תוצאה |
|--------|--------|
| `שלום` | ברכות |
| `עזרה` | תפריט עזרה |
| `קסם` | קסם אקראי |
| `פקודה` | הצעות לפקודות |
| `בנה` | רעיונות לבנייה |
| `הישרדות` | טיפים להישרדות |

## 🌟 תכונות מיוחדות

- **תמיכה בעברית** - דיבור טבעי בעברית
- **קסמים אמיתיים** - שינוי העולם במשחק
- **עזרה חכמה** - טיפים מותאמים אישית
- **שיחה טבעית** - AI שמבין מה אתה רוצה

## 📞 תמיכה

**בעיות? שאלות?**
- פתח Issue ב-GitHub
- צור דיון ב-Discussions
- פנה אלינו בדוא"ל

---

**Magic AI Player** - שחקן AI חכם וקסום למיינקראפט! ✨🎮 