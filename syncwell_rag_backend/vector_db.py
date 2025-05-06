import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer

# Connect to local persistent ChromaDB
# client = chromadb.Client(Settings(chroma_db_impl="duckdb+parquet", persist_directory="chroma_db"))
client = chromadb.PersistentClient(path="chroma_db") 
collection = client.get_or_create_collection(name="syncwell")

# Load SBERT model
model = SentenceTransformer("all-MiniLM-L6-v2")

def search_similar_docs(query: str, phase: str, qtype: str):
    results = collection.query(
        query_texts=[query],
        n_results=10,
        where={"phase": phase, "type": qtype}  # critical for filtering
    )

    documents = results["documents"][0]
    metadatas = results["metadatas"][0]

    filtered_docs = [
        doc for doc, meta in zip(documents, metadatas)
        if meta and meta.get("phase") == phase and meta.get("type") == qtype
    ]

    return filtered_docs
