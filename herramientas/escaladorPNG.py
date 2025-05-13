import tkinter as tk
from tkinter import filedialog
from PIL import Image, ImageTk

def cargar_imagen():
    ruta = filedialog.askopenfilename(filetypes=[("PNG files", "*.png")])
    if not ruta:
        return
    global imagen_original, img_tk
    imagen_original = Image.open(ruta)
    mostrar_imagen(imagen_original)
    ancho_var.set(imagen_original.width)
    alto_var.set(imagen_original.height)

def mostrar_imagen(imagen):
    imagen_miniatura = imagen.copy()
    imagen_miniatura.thumbnail((300, 300))
    img_tk = ImageTk.PhotoImage(imagen_miniatura)
    canvas.create_image(150, 150, image=img_tk)

def redimensionar():
    if imagen_original is None:
        return
    try:
        nuevo_ancho = int(ancho_var.get())
        nuevo_alto = int(alto_var.get())
        imagen_redimensionada = imagen_original.resize((nuevo_ancho, nuevo_alto), Image.LANCZOS)
        guardar_imagen(imagen_redimensionada)
        mostrar_imagen(imagen_redimensionada)
    except Exception as e:
        print("Error:", e)

def guardar_imagen(imagen):
    ruta_guardado = filedialog.asksaveasfilename(defaultextension=".png", filetypes=[("PNG files", "*.png")])
    if ruta_guardado:
        imagen.save(ruta_guardado)

# GUI
root = tk.Tk()
root.title("Redimensionador de Imágenes PNG")

frame = tk.Frame(root)
frame.pack(pady=10)

tk.Button(frame, text="Cargar Imagen PNG", command=cargar_imagen).grid(row=0, column=0, columnspan=2)

tk.Label(frame, text="Nuevo ancho:").grid(row=1, column=0, sticky='e')
ancho_var = tk.StringVar()
tk.Entry(frame, textvariable=ancho_var, width=10).grid(row=1, column=1)

tk.Label(frame, text="Nuevo alto:").grid(row=2, column=0, sticky='e')
alto_var = tk.StringVar()
tk.Entry(frame, textvariable=alto_var, width=10).grid(row=2, column=1)

tk.Button(frame, text="Redimensionar y Guardar", command=redimensionar).grid(row=3, column=0, columnspan=2, pady=5)

canvas = tk.Canvas(root, width=300, height=300, bg='gray')
canvas.pack(pady=10)

imagen_original = None
img_tk = None

root.mainloop()
