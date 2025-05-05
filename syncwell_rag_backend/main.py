from fastapi import FastAPI
from pydantic import BaseModel
from vector_db import search_similar_docs
from gemini_client import generate_answer 

app = FastAPI()

class QueryRequest(BaseModel):
    question: str

@app.post("/query")
def query_rag(request: QueryRequest):
    # Use the user's question directly
    docs = search_similar_docs(request.question)

    # Prepare a prompt with the context documents
    context = "\n".join(docs)
    prompt = f"Based on the following wellness information, answer the question:\n\n{context}\n\nQuestion: {request.question}"

    # Use the wrapped Gemini call
    response = generate_answer(prompt)
    return {"answer": response}
