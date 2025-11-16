from flask import Flask, request, jsonify

app = Flask(__name__)

@app.before_request
def ntlm_stub_auth():
    if request.path != "/process":
        return

    auth = request.headers.get("Authorization")

    if not auth:
        return ("", 401, {"WWW-Authenticate": "NTLM"})

    if not auth.startswith("NTLM "):
        return ("Unsupported auth scheme", 400)

    print("Authorization header:", auth[:80] + ("..." if len(auth) > 80 else ""))

@app.post("/process")
def process():
    body = request.get_json(silent=True)

    return jsonify(
        {
            "status": "OK",
            "note": "NTLM header was present",
            "data": body,
        }
    )

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=3000)
