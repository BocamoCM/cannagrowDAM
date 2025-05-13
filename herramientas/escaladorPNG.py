import tkinter as tk
from tkinter import filedialog, messagebox
from PIL import Image
import os

def seleccionar_carpeta():
    carpeta = filedialog.askdirectory()
    if carpeta:
        carpeta_var.set(carpeta)

def redimensionar_carpeta():
    carpeta = carpeta_var.get()
    if not carpeta or not os.path.isdir(carpeta):
        messagebox.showerror("Error", "Selecciona una carpeta válida.")
        return

    try:
        nuevo_ancho = int(ancho_var.get())
        nuevo_alto = int(alto_var.get())
    except ValueError:
        messagebox.showerror("Error", "Introduce dimensiones válidas.")
        return

    salida = filedialog.askdirectory(title="Selecciona carpeta para guardar resultados")
    if not salida:
        return

    imagenes_procesadas = 0

    for archivo in os.listdir(carpeta):
        if archivo.lower().endswith(".png"):
            ruta_img = os.path.join(carpeta, archivo)
            try:
                img = Image.open(ruta_img)
                img_redimensionada = img.resize((nuevo_ancho, nuevo_alto), Image.LANCZOS)
                img_redimensionada.save(os.path.join(salida, archivo))
                imagenes_procesadas += 1
            except Exception as e:
                print(f"Error procesando {archivo}: {e}")

    messagebox.showinfo("Completado", f"Se procesaron {imagenes_procesadas} imagen(es).")

# GUI
root = tk.Tk()
root.title("Redimensionador de Imágenes PNG por Carpeta")

frame = tk.Frame(root)
frame.pack(pady=10)

tk.Button(frame, text="Seleccionar carpeta con imágenes PNG", command=seleccionar_carpeta).grid(row=0, column=0, columnspan=2)

carpeta_var = tk.StringVar()
tk.Entry(frame, textvariable=carpeta_var, width=50).grid(row=1, column=0, columnspan=2, pady=5)

tk.Label(frame, text="Nuevo ancho:").grid(row=2, column=0, sticky='e')
ancho_var = tk.StringVar()
tk.Entry(frame, textvariable=ancho_var, width=10).grid(row=2, column=1)

tk.Label(frame, text="Nuevo alto:").grid(row=3, column=0, sticky='e')
alto_var = tk.StringVar()
tk.Entry(frame, textvariable=alto_var, width=10).grid(row=3, column=1)

tk.Button(frame, text="Redimensionar imágenes", command=redimensionar_carpeta).grid(row=4, column=0, columnspan=2, pady=10)

root.mainloop()
