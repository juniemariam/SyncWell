import requests
from bs4 import BeautifulSoup
import json

# List of websites to scrape
urls = [
    "https://www.healthline.com/health/fitness/female-hormones-exercise#Phases-of-the-menstrual-cycle"
]

all_texts = []

for url in urls:
    try:
        print(f"Scraping: {url}")
        response = requests.get(url, timeout=10)
        soup = BeautifulSoup(response.text, "html.parser")

        # Extract paragraph text
        paragraphs = soup.find_all("p")
        for p in paragraphs:
            text = p.get_text().strip()
            #if 50 < len(text) < 500:  # Filter out very short/long ones
            all_texts.append({"text": text})

    except Exception as e:
        print(f"Failed to scrape {url}: {e}")

# Save to documents.json
with open("documents.json", "w") as f:
    json.dump(all_texts, f, indent=2)

print(f"Scraped and saved {len(all_texts)} items to documents.json")

