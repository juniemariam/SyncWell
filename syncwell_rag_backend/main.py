from fastapi import FastAPI
from pydantic import BaseModel
from vector_db import search_similar_docs
from gemini_client import generate_answer

app = FastAPI()

class QueryRequest(BaseModel):
    question: str

# Simple rule-based detection of phase and type
def detect_phase_and_type(question: str):
    q = question.lower()
    phase = "unknown"
    qtype = "unknown"

    for p in ["menstrual", "follicular", "ovulation", "ovulatory", "luteal"]:
        if p in q:
            phase = "ovulatory" if p == "ovulation" else p
            break

    if any(x in q for x in ["diet", "food", "nutrition", "eat", "meal"]):
        qtype = "diet"
    elif any(x in q for x in ["exercise", "workout", "train", "physical activity"]):
        qtype = "exercise"

    return phase, qtype

@app.post("/query")
def query_rag(request: QueryRequest):
    question = request.question
    phase, qtype = detect_phase_and_type(question)

    if qtype == "unknown":
        return {
                "answer": "I'm your health assistant here to help with diet and exercise advice across the menstrual cycle. "
                "I couldn’t determine whether your question was about diet or exercise. Could you please rephrase or be more specific?"
            }

    # Fallback: Search across all phases if phase is unknown
    possible_phases = [phase] if phase != "unknown" else ["menstrual", "follicular", "ovulatory", "luteal"]
    
    docs = []
    for p in possible_phases:
        docs += search_similar_docs(question, p, qtype)

    if not docs:
        return {"answer": "Sorry, I couldn’t find relevant information in the knowledge base."}

    # Construct prompt for Gemini
    context = "\n".join(docs)
    phase_info = phase if phase != "unknown" else "the appropriate menstrual phase"
    prompt = (
        f"You are a helpful health assistant. Based on the following information specific to {phase_info} "
        f"and focused on {qtype}, answer the question.\n\n"
        f"{context}\n\n"
        f"Question: {question}"
    )

    response = generate_answer(prompt)
    return {"answer": response}
