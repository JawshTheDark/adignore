# AdIgnore (Meteor Addon)

A Meteor Client addon that automatically blocks chat spam by ignoring players and filtering messages based on configurable keywords.

Built for anarchy/chaotic servers where Discord ads, bot spam, and kit promotions flood chat.

---

## ✨ Features

- 🔇 Ignore players by name with reasons
- 🧠 Keyword filtering (e.g. discord.gg, free kits)
- 🤖 Auto-add spammers to ignore list
- 📁 JSON config (ignored.json, keywords.json)
- 🔄 Live reload via module toggle
- 💬 .ignore command with autocomplete
- 🧪 Optional debug logging

---

## 📂 File Locations

meteor-client/adignore/

### ignored.json
Stores ignored players.

### keywords.json
Stores keyword filters.

---

## ⚙️ Usage

Enable module in Meteor → AdIgnore → ad-ignore

Add player:
.ignore <name> <reason>

Reload configs:
Toggle "reload-files" in module settings

---

## 🧠 How It Works

Intercepts chat → extracts sender → checks:
- keyword → block + auto-add
- ignored user → block

---

## 🔥 Supported Formats

- <Player> message
- Player: message
- [Rank] Player: message
- Player » message
- Player whispers: message

---

## ⚠️ Notes

- Autocomplete = online players only
- Keyword matching = case-insensitive
- JSON edits require reload

---

## 🛠️ Build

./gradlew build

Output:
build/libs/adignore-<version>.jar

---

## 📜 License

MIT
