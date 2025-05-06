import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer

client = chromadb.PersistentClient(path="chroma_db")
collection = client.get_or_create_collection(name="syncwell")

model = SentenceTransformer("all-MiniLM-L6-v2")

def search_similar_docs(query: str, phase: str, qtype: str):
    embedding = model.encode([query]).tolist()

    # Fix: Use $and for multiple filters
    filters = {
        "$and": [
            {"phase": phase},
            {"type": qtype}
        ]
    }

    results = collection.query(
        query_embeddings=embedding,
        n_results=5,
        where=filters
    )

    documents = results.get("documents", [[]])[0]
    return documents if documents else ["I couldn't find relevant documents in the database."]
