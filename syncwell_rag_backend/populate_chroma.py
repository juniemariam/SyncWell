import json
import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer

# Load JSON document file
with open("documents.json", "r") as f:
    docs = json.load(f)

texts = [doc["text"] for doc in docs]
ids = [f"doc_{i}" for i in range(len(texts))]
metadatas = [{"phase": doc["phase"], "type": doc["type"]} for doc in docs]

# Load SBERT model and embed texts
model = SentenceTransformer("all-MiniLM-L6-v2")
embeddings = model.encode(texts).tolist()

# Connect to ChromaDB
client = chromadb.PersistentClient(path="chroma_db") 
collection = client.get_or_create_collection(name="syncwell")

# Insert documents with metadata
collection.add(documents=texts, embeddings=embeddings, metadatas=metadatas, ids=ids)

print(f"Inserted {len(texts)} documents with metadata into ChromaDB.")
