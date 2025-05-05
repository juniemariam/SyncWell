import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer

# Connect to local persistent ChromaDB
# client = chromadb.Client(Settings(chroma_db_impl="duckdb+parquet", persist_directory="chroma_db"))
client = chromadb.PersistentClient(path="chroma_db") 
collection = client.get_or_create_collection(name="syncwell")

# Load SBERT model
model = SentenceTransformer("all-MiniLM-L6-v2")

def search_similar_docs(query: str, top_k=3):
    query_embedding = model.encode(query).tolist()
    results = collection.query(
        query_embeddings=[query_embedding],
        n_results=top_k
    )
    return results["documents"][0]
