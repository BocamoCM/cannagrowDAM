# conversor.py
# Script para convertir imágenes JPG/PNG a WebP con interfaz gráfica.
# Requisitos: Python 3.x, Pillow (instalar con `pip install pillow`).
# Requisitos: Python 3.x, Pillow (instalar con `pip install pillow`).

import os
import sys
import tkinter as tk
from tkinter import filedialog, messagebox

# Intentamos importar PIL y avisamos si falta
try:
    from PIL import Image
except ImportError:
    message = (
        "La librería Pillow no está instalada.\n"
        "Instálala ejecutando `pip install pillow` en tu consola."
    )
    tk.messagebox.showerror("Dependencia faltante", message)
    sys.exit(1)


def select_images():
    filetypes = [
        ("Imágenes JPEG", "*.jpg;*.jpeg"),
        ("Imágenes PNG", "*.png"),
        ("Todos los archivos", "*.*")
    ]
    files = filedialog.askopenfilenames(
        title="Selecciona imágenes (JPG/PNG)",
        filetypes=filetypes
    )
    if files:
        output_dir = filedialog.askdirectory(title="Selecciona carpeta de salida")
        if not output_dir:
            messagebox.showwarning(
                "Directorio no seleccionado",
                "Debes seleccionar un directorio de salida."
            )
            return
        convert_images(files, output_dir)


def convert_images(files, output_dir):
    count = 0
    for filepath in files:
        try:
            img = Image.open(filepath)
            basename = os.path.splitext(os.path.basename(filepath))[0]
            output_path = os.path.join(output_dir, basename + ".webp")
            img.save(output_path, "WEBP")
            count += 1
        except Exception as e:
            messagebox.showerror(
                f"Error al convertir {os.path.basename(filepath)}",
                str(e)
            )
    messagebox.showinfo(
        "Conversión completada",
        f"{count} imagen(es) convertida(s) a WebP."
    )


def create_gui():
    root = tk.Tk()
    root.title("Conversor JPG/PNG a WebP")
    root.geometry("400x200")
    root.resizable(False, False)

    frame = tk.Frame(root, padx=20, pady=20)
    frame.pack(expand=True)

    label = tk.Label(
        frame,
        text="Convierte tus imágenes JPG o PNG a WebP",
        font=(None, 14)
    )
    label.pack(pady=(0,10))

    btn_select = tk.Button(
        frame,
        text="Seleccionar imágenes",
        command=select_images
    )
    btn_select.pack(pady=(0,10), fill='x')

    btn_exit = tk.Button(
        frame,
        text="Salir",
        command=root.quit
    )
    btn_exit.pack(fill='x')

    root.mainloop()


if __name__ == "__main__":
    create_gui()
