from fastapi import FastAPI
from pydantic import BaseModel
from vector_db import search_similar_docs
from gemini_client import generate_answer

app = FastAPI()

class QueryRequest(BaseModel):
    question: str

# Simple keyword-based phase/type detection
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
    # Step 1: Detect phase and type from question
    phase, qtype = detect_phase_and_type(request.question)

    if phase == "unknown" or qtype == "unknown":
        return {"answer": "Sorry, I couldn’t determine the phase or type of question. Please rephrase."}

    # Step 2: Retrieve filtered documents
    docs = search_similar_docs(request.question, phase, qtype)

    # Step 3: Construct RAG prompt
    context = "\n".join(docs)
    prompt = (
        f"You are a helpful health assistant. Based on the following information specific to the {phase} phase "
        f"and focused on {qtype}, answer the question.\n\n"
        f"{context}\n\n"
        f"Question: {request.question}"
    )

    # Step 4: Get response from Gemini
    response = generate_answer(prompt)
    return {"answer": response}
