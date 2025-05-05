import json

# List of wellness tips
wellness_data = [
    "Drink more water every day.",
    "Exercise at least 30 minutes a day.",
    "Eat a balanced diet rich in fruits and vegetables.",
    "Get at least 7–8 hours of sleep per night.",
    "Practice mindfulness or meditation daily.",
    "Avoid processed sugars for improved energy levels.",
    "Take short walks during your workday to stay active."
]

# Convert to the expected format
documents = [{"text": tip} for tip in wellness_data]

# Save to JSON file
with open("documents.json", "w") as f:
    json.dump(documents, f, indent=2)

print(f"Saved {len(documents)} wellness tips to documents.json")
