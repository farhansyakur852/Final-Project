from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import re

import nltk
from nltk.tokenize import word_tokenize
from Sastrawi.Stemmer.StemmerFactory import StemmerFactory

# Pastikan nltk resources sudah diunduh
# nltk.download('stopwords')
# nltk.download('wordnet')
# nltk.download('punkt')
# nltk.download('punkt_tab')

app = FastAPI()

# Load model and vectorizer
model = joblib.load("spam_model/spam_model_smote.pkl")
vectorizer = joblib.load("spam_model/tfidf_vectorizer_smote.pkl")

# Preprocessing function
def preprocess_text(text):
    factory = StemmerFactory()
    stemmer = factory.create_stemmer()

    text = text.lower()
    text = re.sub(r'[^a-z\s]', ' ', text)
    tokens = word_tokenize(text)
    tokens = [stemmer.stem(word) for word in tokens]
    return ' '.join(tokens)

# Input schema
class PredictRequest(BaseModel):
    text: str

@app.post("/predict/")
async def predict(request: PredictRequest):
    try:
        preprocessed_text = preprocess_text(request.text)
        vectorized_text = vectorizer.transform([preprocessed_text])
        prediction = model.predict(vectorized_text)
        return {"text": request.text, "prediction": prediction[0]}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
