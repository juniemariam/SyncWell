import json
import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer

# Load JSON document file
with open("documents.json", "r") as f:
    docs = json.load(f)

texts = [doc["text"] for doc in docs]
ids = [f"doc_{i}" for i in range(len(texts))]

# Embed texts
model = SentenceTransformer("all-MiniLM-L6-v2")
embeddings = model.encode(texts).tolist()

# Set up ChromaDB (stored in local folder)
# client = chromadb.Client(Settings(chroma_db_impl="duckdb+parquet", persist_directory="chroma_db"))
# collection = client.get_or_create_collection(name="syncwell")
client = chromadb.PersistentClient(path="chroma_db") 
collection = client.get_or_create_collection(name="syncwell")

# Insert data
collection.add(documents=texts, embeddings=embeddings, ids=ids)
print(f"Inserted {len(texts)} documents into ChromaDB.")
