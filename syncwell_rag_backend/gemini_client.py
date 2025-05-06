import os
from dotenv import load_dotenv
import google.generativeai as genai

load_dotenv()
genai.configure(api_key=os.getenv("GEMINI_API_KEY"))

# Correct usage of Gemini chat
model = genai.GenerativeModel("gemini-2.0-flash")
chat = model.start_chat()

def generate_answer(prompt: str):
    response = chat.send_message(prompt)
    return response.text
