from PIL import Image
import numpy as np
import os

def quitar_fondo(ruta_imagen, color_fondo=(255, 255, 255), tolerancia=30):
    imagen = Image.open(ruta_imagen).convert("RGBA")
    datos = np.array(imagen)

    r, g, b, a = datos[:, :, 0], datos[:, :, 1], datos[:, :, 2], datos[:, :, 3]
    fondo_r, fondo_g, fondo_b = color_fondo

    # Creamos una máscara de píxeles similares al color de fondo
    mascara = (np.abs(r - fondo_r) <= tolerancia) & \
              (np.abs(g - fondo_g) <= tolerancia) & \
              (np.abs(b - fondo_b) <= tolerancia)

    # Establecemos la opacidad (alpha) a 0 en esos píxeles
    datos[mascara, 3] = 0

    nueva_imagen = Image.fromarray(datos, "RGBA")
    return nueva_imagen

def procesar_carpeta(carpeta_entrada, carpeta_salida):
    if not os.path.exists(carpeta_salida):
        os.makedirs(carpeta_salida)

    for archivo in os.listdir(carpeta_entrada):
        if archivo.lower().endswith(".png"):
            ruta = os.path.join(carpeta_entrada, archivo)
            print(f"Procesando {archivo}...")
            imagen_sin_fondo = quitar_fondo(ruta)
            ruta_guardado = os.path.join(carpeta_salida, archivo)
            imagen_sin_fondo.save(ruta_guardado)
    print("¡Proceso completado!")

# Configuración: cambia las rutas según tu carpeta
carpeta_entrada = "imagenes_png"
carpeta_salida = "imagenes_sin_fondo"

procesar_carpeta(carpeta_entrada, carpeta_salida)
