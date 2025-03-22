import os
import piexif
from google import genai
from google.genai import types

def get_image_timestamp(image_path):
    """Extract the DateTimeOriginal from image EXIF data."""
    try:
        exif_data = piexif.load(image_path)
        datetime_original = exif_data["Exif"].get(piexif.ExifIFD.DateTimeOriginal)
        return datetime_original.decode("utf-8") if datetime_original else "Timestamp not available"
    except Exception as e:
        return f"Error extracting timestamp: {e}"

def generate():
    client = genai.Client(api_key=os.environ.get("GEMINI_API_KEY"))

    # Ask user for image path dynamically
    image_path = input("Enter the image file path: ").strip()

    if not os.path.exists(image_path):
        print("Error: File not found. Please enter a valid file path.")
        return

    timestamp = get_image_timestamp(image_path)

    try:
        uploaded_file = client.files.upload(file=image_path)  # Upload image

        contents = [
            types.Content(
                role="user",
                parts=[
                    types.Part.from_uri(
                        file_uri=uploaded_file.uri,
                        mime_type=uploaded_file.mime_type,
                    ),
                    types.Part.from_text(text=f"Timestamp: {timestamp}"),  
                ],
            ),
        ]
    except:
        contents = [
            types.Content(
                role="user",
                parts=[
                    types.Part.from_text(text="""The user uploaded an image whereby the format is not supported. Please tell the user to upload a valid image file, that is jpeg or jpg."""),
                    ],
            )
        ]

    model = "gemini-2.0-flash"
    generate_content_config = types.GenerateContentConfig(
        temperature=1,
        top_p=0.95,
        top_k=40,
        max_output_tokens=8192,
        response_mime_type="text/plain",
        system_instruction=[types.Part.from_text(text="Describe the image and mention when it was taken.")],
    )

    for chunk in client.models.generate_content_stream(model=model, contents=contents, config=generate_content_config):
        print(chunk.text, end="")

if __name__ == "__main__":
    generate()
