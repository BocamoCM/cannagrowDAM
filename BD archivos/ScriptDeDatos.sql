-- Plantilla

-- PRODUCTO
-- INSERT INTO Producto (nombre, tipo, contenidoTHC, contenidoCBD, precio, stock)
-- VALUES ('NombreProducto', 'Flor', 18.5, 0.2, 25.99, 100);

-- CLIENTE
-- INSERT INTO Cliente (nombre, fechaNacimiento, email, direccion, contrasena)
-- VALUES ('Juan Pérez','1990-03-25', 'juan.perez@email.com', 'Calle Falsa 123, Ciudad', 'as123sd');

-- EMPLEADO
-- INSERT INTO Empleado (nombre, rol, email, salario, contrasena)
-- VALUES ('María Gómez', '55120', 'Vendedor', 'maria.gomez@email.com', 2500.00, 'as123sd');

-- PEDIDO
-- INSERT INTO Pedido (fecha, total, estado, cliente_id)
-- VALUES ('2025-05-01', 0.0, 'Pendiente', 1); -- Asumiendo que el cliente con id=1 ya existe

-- ITEMPEDIDO
-- INSERT INTO ItemPedido (pedido_id, producto_id, cantidad)
-- VALUES (1, 1, 2); -- Asume que pedido_id=1 y producto_id=1 existen

-- REGULACION
-- INSERT INTO Regulacion (descripcion)
-- VALUES ('Producto no debe exceder 20% de THC para venta libre.');

-- DATOS EMPLEADOS
INSERT INTO Empleado (nombre, rol, email, salario, contrasena_hash) 
VALUES 
  ('Joaquin Tena',   'Gerente',    'joaquin_tena@email.com',   4400.00, '<HASH_BCRYPT>'),
  ('Borja Carreres', 'Gerente',    'borja_carreres@email.com', 6400.00, '<HASH_BCRYPT>'),
  ('Andreu Rosell',  'Gerente',    'andreu_rosell@email.com', 10000.00, '<HASH_BCRYPT>'),
  ('Nacho Piles',    'Cultivador', 'nacho_piles@email.com',   1400.00, '<HASH_BCRYPT>'),
  ('Mario mas',      'Vendedor',   'mario_mas@email.com',     1850.00, '<HASH_BCRYPT>'),
  ('Lucía Vega',     'Vendedor',   'lucia.vega@email.com',    2100.00, '<HASH_BCRYPT>'),
  ('Carlos Ruiz',    'Vendedor',   'carlos.ruiz@email.com',   1300.00, '<HASH_BCRYPT>'),
  ('Ana Torres',     'Vendedor',   'ana.torres@email.com',    1950.00, '<HASH_BCRYPT>'),
  ('José Morales',   'Vendedor',   'jose.morales@email.com',  1400.00, '<HASH_BCRYPT>'),
  ('Elena Bravo',    'Vendedor',   'elena.bravo@email.com',   1750.00, '<HASH_BCRYPT>'),
  ('Luis Cano',      'Vendedor',   'luis.cano@email.com',     2200.00, '<HASH_BCRYPT>'),
  ('Marta Gil',      'Vendedor',   'marta.gil@email.com',     1600.00, '<HASH_BCRYPT>'),
  ('Pablo Díaz',     'Vendedor',   'pablo.diaz@email.com',    1200.00, '<HASH_BCRYPT>'),
  ('Sandra León',    'Vendedor',   'sandra.leon@email.com',   2050.00, '<HASH_BCRYPT>'),
  ('Jorge Paredes',  'Cultivador', 'jorge.paredes@email.com', 1450.00, '<HASH_BCRYPT>'),
  ('Claudia Rivas',  'Cultivador', 'claudia.rivas@email.com', 1000.00, '<HASH_BCRYPT>'),
  ('Iván Soto',      'Cultivador', 'ivan.soto@email.com',     1300.00, '<HASH_BCRYPT>'),
  ('Verónica Castro','Cultivador', 'veronica.castro@email.com',1100.00, '<HASH_BCRYPT>'),
  ('Pedro Navas',    'Cultivador', 'pedro.navas@email.com',   1200.00, '<HASH_BCRYPT>'),
  ('Camila Durán',   'Cultivador', 'camila.duran@email.com',   950.00, '<HASH_BCRYPT>'),
  ('Andrés Muñoz',   'Cultivador', 'andres.munoz@email.com',  1250.00, '<HASH_BCRYPT>'),
  ('Raquel Romero',  'Cultivador', 'raquel.romero@email.com', 1350.00, '<HASH_BCRYPT>'),
  ('David Salas',    'Cultivador', 'david.salas@email.com',   1150.00, '<HASH_BCRYPT>'),
  ('Sofía Méndez',   'Cultivador', 'sofia.mendez@email.com',   900.00, '<HASH_BCRYPT>'),
  ('Esteban Lara',   'Cultivador', 'esteban.lara@email.com',  1420.00, '<HASH_BCRYPT>'),
  ('Paula Guzmán',   'Cultivador', 'paula.guzman@email.com',  1280.00, '<HASH_BCRYPT>'),
  ('Héctor Arias',   'Cultivador', 'hector.arias@email.com',  1490.00, '<HASH_BCRYPT>'),
  ('Laura Peña',     'Cultivador', 'laura.pena@email.com',    1380.00, '<HASH_BCRYPT>'),
  ('Rafael Valdés',  'Cultivador', 'rafael.valdes@email.com',  920.00, '<HASH_BCRYPT>'),
  ('Natalia Prado',  'Cultivador', 'natalia.prado@email.com', 1050.00, '<HASH_BCRYPT>'),
  ('Bruno Ortiz',    'Cultivador', 'bruno.ortiz@email.com',   1320.00, '<HASH_BCRYPT>'),
  ('Valentina Mora', 'Cultivador', 'valentina.mora@email.com',1430.00, '<HASH_BCRYPT>'),
  ('Diego Cordero',  'Cultivador', 'diego.cordero@email.com', 1170.00, '<HASH_BCRYPT>'),
  ('Isabel Rico',    'Cultivador', 'isabel.rico@email.com',   1250.00, '<HASH_BCRYPT>'),
  ('Gabriel Pinto',  'Cultivador', 'gabriel.pinto@email.com', 1400.00, '<HASH_BCRYPT>'),
  ('Emma Molina',    'Cultivador', 'emma.molina@email.com',   1180.00, '<HASH_BCRYPT>');


-- Clientes
INSERT INTO Cliente (nombre, fechaNacimiento, email, direccion, contrasena_hash) VALUES 
  ('Juan Pérez', 			 '1990-03-25', 'juan.perez@email.com',            'Calle de la tortosa, Llauri',                                    '<HASH_BCRYPT>'),
  ('Sara García',            '1954-11-13', 'sara.garcia@example.com',           'Calle de la tortosa 79, Benifaió',                               '<HASH_BCRYPT>'),
  ('Miguel Gómez',           '1969-01-23', 'miguel.gomez@example.com',          'Calle de la tortosa 104, Turís',                                 '<HASH_BCRYPT>'),
  ('Isabel Gómez',           '1976-09-09', 'isabel.gomez@example.com',          'Calle de la tortosa 76, Xeraco',                                 '<HASH_BCRYPT>'),
  ('Juan Gómez',             '1989-04-19', 'juan.gomez@example.com',            'Calle de la tortosa 108, Alzira',                                '<HASH_BCRYPT>'),
  ('Ana Martín',             '2002-07-21', 'ana.martin@example.com',            'Calle de la tortosa 90, Llaurí',                                 '<HASH_BCRYPT>'),
  ('Patricia García',        '1977-01-23', 'patricia.garcia@example.com',       'Calle de la tortosa 69, Carcaixent',                             '<HASH_BCRYPT>'),
  ('José Martín',            '1967-05-27', 'jose.martin@example.com',           'Calle de la tortosa 59, Benicull de Xúquer',                     '<HASH_BCRYPT>'),
  ('José Jiménez',           '1976-07-23', 'jose.jimenez@example.com',          'Calle de la tortosa 121, Miramar',                               '<HASH_BCRYPT>'),
  ('Nuria Rodríguez',        '1986-05-21', 'nuria.rodriguez@example.com',       'Calle de la tortosa 98, Tavernes de la Valldigna',               '<HASH_BCRYPT>'),
  ('María Martín',           '1999-12-23', 'maria.martin@example.com',          'Calle de la tortosa 63, Algemesí',                               '<HASH_BCRYPT>'),
  ('Lucía Rodríguez',        '2005-12-05', 'lucia.rodriguez@example.com',       'Calle de la tortosa 34, Favara',                                 '<HASH_BCRYPT>'),
  ('Carlos Martínez',        '1967-06-26', 'carlos.martinez@example.com',       'Calle de la tortosa 134, Algemesí',                              '<HASH_BCRYPT>'),
  ('Raúl Jiménez',           '1969-01-01', 'raul.jimenez@example.com',          'Calle de la tortosa 5, Carcaixent',                              '<HASH_BCRYPT>'),
  ('Pedro López',            '1959-09-28', 'pedro.lopez@example.com',           'Calle de la tortosa 116, Alberic',                               '<HASH_BCRYPT>'),
  ('Francisco Martín',       '1978-05-08', 'francisco.martin@example.com',      'Calle de la tortosa 46, Miramar',                                '<HASH_BCRYPT>'),
  ('José Sánchez',           '1980-11-02', 'jose.sanchez@example.com',          'Calle de la tortosa 82, Cullera',                                '<HASH_BCRYPT>'),
  ('Laura Ruiz',             '1955-07-19', 'laura.ruiz@example.com',            'Calle de la tortosa 155, Montserrat',                            '<HASH_BCRYPT>'),
  ('David García',           '1960-10-14', 'david.garcia@example.com',          'Calle de la tortosa 14, Sueca',                                  '<HASH_BCRYPT>'),
  ('Fernando Martín',        '1975-02-25', 'fernando.martin@example.com',       'Calle de la tortosa 99, Polinyà de Xúquer',                      '<HASH_BCRYPT>'),
  ('Rosa López',             '1984-06-30', 'rosa.lopez@example.com',            'Calle de la tortosa 27, Carlet',                                 '<HASH_BCRYPT>'),
  ('Antonio Fernández',      '1951-01-12', 'antonio.fernandez@example.com',     'Calle de la tortosa 48, Alginet',                                '<HASH_BCRYPT>'),
  ('Carmen Ruiz',            '1988-03-17', 'carmen.ruiz@example.com',           'Calle de la tortosa 113, Llaurí',                                '<HASH_BCRYPT>'),
  ('Arturo Gómez',           '1963-12-05', 'arturo.gomez@example.com',          'Calle de la tortosa 37, Riola',                                  '<HASH_BCRYPT>'),
  ('Pilar Martín',           '1995-09-07', 'pilar.martin@example.com',          'Calle de la tortosa 67, Turís',                                  '<HASH_BCRYPT>'),
  ('Sonia García',           '1989-05-10', 'sonia.garcia@example.com',          'Calle de la tortosa 104, Algemesí',                              '<HASH_BCRYPT>'),
  ('Beatriz López',          '1974-10-12', 'beatriz.lopez@example.com',         'Calle de la tortosa 20, Xeraco',                                 '<HASH_BCRYPT>'),
  ('Jorge Sánchez',          '2000-08-08', 'jorge.sanchez@example.com',         'Calle de la tortosa 58, Cullera',                                '<HASH_BCRYPT>'),
  ('Marta González',         '1982-04-22', 'marta.gonzalez@example.com',        'Calle de la tortosa 132, Gandia',                                '<HASH_BCRYPT>'),
  ('Lucía Martín',           '1957-08-29', 'lucia.martin@example.com',          'Calle de la tortosa 61, Favara',                                 '<HASH_BCRYPT>'),
  ('Francisco López',        '1973-07-26', 'francisco.lopez@example.com',       'Calle de la tortosa 179, Miramar',                               '<HASH_BCRYPT>'),
  ('Rafael García',          '1950-02-15', 'rafael.garcia@example.com',         'Calle de la tortosa 73, Albalat de la Ribera',                   '<HASH_BCRYPT>'),
  ('Natalia Rodríguez',      '1966-09-23', 'natalia.rodriguez@example.com',     'Calle de la tortosa 143, Corbera',                               '<HASH_BCRYPT>'),
  ('Diego Sánchez',          '1962-11-11', 'diego.sanchez@example.com',         'Calle de la tortosa 19, Cullera',                                '<HASH_BCRYPT>'),
  ('Emma Ruiz',              '1987-12-09', 'emma.ruiz@example.com',             'Calle de la tortosa 154, Carcaixent',                            '<HASH_BCRYPT>'),
  ('Isabel García',          '1966-08-21', 'isabel.garcia@example.com',         'Calle de la tortosa 36, Montserrat',                             '<HASH_BCRYPT>'),
  ('Lucas Martín',           '2003-03-15', 'lucas.martin@example.com',          'Calle de la tortosa 29, Riola',                                  '<HASH_BCRYPT>'),
  ('Ana López',              '1958-05-23', 'ana.lopez@example.com',             'Calle de la tortosa 88, Sueca',                                  '<HASH_BCRYPT>'),
  ('Carlos García',          '1971-01-30', 'carlos.garcia@example.com',         'Calle de la tortosa 105, Gavarda',                               '<HASH_BCRYPT>'),
  ('María Rodríguez',        '1985-10-19', 'maria.rodriguez@example.com',       'Calle de la tortosa 12, Bellreguard',                            '<HASH_BCRYPT>'),
  ('Pedro Ruiz',             '1992-04-04', 'pedro.ruiz@example.com',            'Calle de la tortosa 147, Benifaió',                              '<HASH_BCRYPT>'),
  ('Laura Martín',           '1964-06-06', 'laura.martin@example.com',          'Calle de la tortosa 33, Barx',                                   '<HASH_BCRYPT>'),
  ('José García',            '1953-03-21', 'jose.garcia@example.com',           'Calle de la tortosa 77, Turís',                                  '<HASH_BCRYPT>'),
  ('Sara López',             '1972-11-15', 'sara.lopez@example.com',            'Calle de la tortosa 91, Sueca',                                  '<HASH_BCRYPT>'),
  ('Juan Ruiz',              '1981-08-02', 'juan.ruiz@example.com',             'Calle de la tortosa 102, Sollana',                               '<HASH_BCRYPT>'),
  ('Ana García',             '1976-09-23', 'ana.garcia36@example.com',          'Calle de la tortosa 143, Corbera',                               '<HASH_BCRYPT>'),
  ('Jorge Rodríguez',        '2004-05-15', 'jorge.rodriguez@example.com',       'Calle de la tortosa 154, Carlet',                                '<HASH_BCRYPT>'),
  ('Elena Ruiz',             '1967-12-19', 'elena.ruiz@example.com',            'Calle de la tortosa 82, Barx',                                   '<HASH_BCRYPT>'),
  ('Sonia Martín',           '1989-05-10', 'sonia.martin@example.com',          'Calle de la tortosa 104, Algemesí',                              '<HASH_BCRYPT>'),
  ('Pilar García',           '1952-05-31', 'pilar.garcia@example.com',          'Calle de la tortosa 74, Gandia',                                 '<HASH_BCRYPT>'),
  ('Fernando García',        '1989-07-09', 'fernando.garcia61@example.com',     'Calle de la tortosa 104, Turís',                                 '<HASH_BCRYPT>'),
  ('Pedro Sánchez',          '1950-06-21', 'pedro.sanchez@example.com',         'Calle de la tortosa 190, Carlet',                                '<HASH_BCRYPT>'),
  ('Rosa Martín',            '1973-04-15', 'rosa.martin@example.com',           'Calle de la tortosa 39, Bellreguard',                            '<HASH_BCRYPT>'),
  ('Beatriz Martín',         '1974-10-12', 'beatriz.martin@example.com',        'Calle de la tortosa 20, Xeraco',                                 '<HASH_BCRYPT>'),
  ('Javier Navarro',         '1991-09-04', 'javier.navarro@example.com',        'Calle de la tortosa 195, Tavernes de la Valldigna',              '<HASH_BCRYPT>'),
  ('Miguel Navarro',         '1987-03-29', 'miguel.navarro@example.com',        'Calle de la tortosa 145, Xeresa',                                '<HASH_BCRYPT>'),
  ('Lucía Navarro',          '1951-12-27', 'lucia.navarro@example.com',         'Calle de la tortosa 80, Fortaleny',                              '<HASH_BCRYPT>'),
  ('Antonio Hernández',      '1959-11-21', 'antonio.hernandez@example.com',     'Calle de la tortosa 132, Carcaixent',                            '<HASH_BCRYPT>'),
  ('Clara Navarro',          '1993-04-08', 'clara.navarro@example.com',         'Calle de la tortosa 126, Algemesí',                              '<HASH_BCRYPT>'),
  ('Javier Gil',             '2004-10-03', 'javier.gil@example.com',            'Calle de la tortosa 124, Alberic',                               '<HASH_BCRYPT>'),
  ('Diego Pérez',            '1968-04-03', 'diego.perez@example.com',           'Calle de la tortosa 14, Corbera',                                '<HASH_BCRYPT>'),
  ('Mario Gil',              '1999-05-17', 'mario.gil@example.com',             'Calle de la tortosa 11, Barx',                                   '<HASH_BCRYPT>'),
  ('Lucas Ortega',           '2005-03-05', 'lucas.ortega@example.com',          'Calle de la tortosa 131, Ador',                                  '<HASH_BCRYPT>'),
  ('Diego Pérez',            '1957-10-27', 'diego.perez60@example.com',         'Calle de la tortosa 70, Algemesí',                               '<HASH_BCRYPT>'),
  ('Alba Gil',               '1982-03-05', 'alba.gil@example.com',              'Calle de la tortosa 75, Xeraco',                                 '<HASH_BCRYPT>'),
  ('Manuel Molina',          '1952-05-24', 'manuel.molina@example.com',         'Calle de la tortosa 16, Sueca',                                  '<HASH_BCRYPT>'),
  ('Fernando Santiago',      '1961-08-24', 'fernando.santiago@example.com',     'Calle de la tortosa 189, Carlet',                                '<HASH_BCRYPT>'),
  ('Miguel Pérez',           '1959-05-25', 'miguel.perez@example.com',          'Calle de la tortosa 187, Ador',                                  '<HASH_BCRYPT>'),
  ('Mario Castillo',         '1998-02-23', 'mario.castillo@example.com',        'Calle de la tortosa 71, Carlet',                                 '<HASH_BCRYPT>'),
  ('Clara Hernández',        '1972-02-29', 'clara.hernandez@example.com',       'Calle de la tortosa 64, Montserrat',                             '<HASH_BCRYPT>'),
  ('Ignacio Navarro',        '1982-12-29', 'ignacio.navarro@example.com',       'Calle de la tortosa 86, Xeraco',                                 '<HASH_BCRYPT>'),
  ('Javier Hernández',       '1955-05-02', 'javier.hernandez@example.com',      'Calle de la tortosa 93, Bellreguard',                            '<HASH_BCRYPT>'),
  ('Sara Ortega',            '1990-03-15', 'sara.ortega@example.com',           'Calle de la tortosa 3, Cullera',                                 '<HASH_BCRYPT>'),
  ('Irene Hernández',        '1987-06-23', 'irene.hernandez@example.com',       'Calle de la tortosa 131, Almussafes',                            '<HASH_BCRYPT>'),
  ('Laura Navarro',          '1957-11-25', 'laura.navarro@example.com',         'Calle de la tortosa 183, Carcaixent',                            '<HASH_BCRYPT>'),
  ('Raúl Hernández',         '2002-07-27', 'raul.hernandez@example.com',        'Calle de la tortosa 122, Barx',                                  '<HASH_BCRYPT>'),
  ('Eduardo Hernández',      '1962-12-07', 'eduardo.hernandez90@example.com',   'Calle de la tortosa 182, Alginet',                               '<HASH_BCRYPT>'),
  ('Beatriz Ruiz',           '2004-06-19', 'beatriz.ruiz@example.com',          'Calle de la tortosa 51, Cullera',                                '<HASH_BCRYPT>'),
  ('Cristina Pérez',         '1971-12-02', 'cristina.perez@example.com',        'Calle de la tortosa 137, Carlet',                                '<HASH_BCRYPT>'),
  ('Fernando Ortega',        '1989-01-27', 'fernando.ortega@example.com',       'Calle de la tortosa 175, Daimús',                                '<HASH_BCRYPT>'),
  ('Paula Molina',           '1968-01-31', 'paula.molina93@example.com',        'Calle de la tortosa 125, Sueca',                                 '<HASH_BCRYPT>'),
  ('Adrián Ortega',          '1978-06-16', 'adrian.ortega@example.com',         'Calle de la tortosa 187, Ador',                                  '<HASH_BCRYPT>'),
  ('Laura Hernández',        '1971-04-11', 'laura.hernandez@example.com',       'Calle de la tortosa 121, Sueca',                                 '<HASH_BCRYPT>'),
  ('Antonio Hernández',      '2000-09-15', 'antonio.hernandez8@example.com',    'Calle de la tortosa 163, Polinyà de Xúquer',                     '<HASH_BCRYPT>');









-- PRODUCTOS
INSERT INTO Producto (nombre, tipo, contenidoTHC, contenidoCBD, precio, stock) VALUES 
('SemillaSinSeleccionar', 'semilla', 18.5, 0.2, 15.99, 100),
('SemillaSelecionadaNormal', 'semilla', 18.5, 0.2, 22.45, 180),
('SemillaSelecionadaPremium', 'semilla', 18.5, 0.2, 40.97, 25),
('LamparaBasicaPersonal', 'Artilujos', 18.5, 0.2, 20.97, 15),
('LamparaPersonal', 'Artilujos', 18.5, 0.2, 24.97, 18),
('LamparaIndustrialBasica', 'Artilujos', 18.5, 0.2, 100.97, 15),
('LamparaIndustrial', 'Artilujos', 18.5, 0.2, 170.97, 15),
('LamparaIndustrialGrande', 'Artilujos', 18.5, 0.2, 365.97, 15),
('PaqueteIndustriaBasico', 'Paquete', 18.5, 0.2, 752.97, 3),
('PaquetePersonalBasico', 'Paquete', 18.5, 0.2, 60.97, 15),
('PaquetePersonal', 'Paquete', 18.5, 0.2, 69.97, 15),
('PaquetePersonalCompleto', 'Paquete', 18.5, 0.2, 123.00, 15),
('CamisaBasicaNegra', 'ropa', 18.5, 0.2, 12.97, 15),
('CamisaBasicaBlanca', 'ropa', 18.5, 0.2, 12.47, 15),
('PantalonEmpresa', 'ropa', 18.5, 0.2, 19.97, 55),
('Abono', 'recurso', 18.5, 0.2, 12.97, 15),
('Agua5Litro', 'recurso', 18.5, 0.2, 3.97, 15),
('Agua25Litro', 'recurso', 18.5, 0.2, 14.97, 15),
('Agua100Litro', 'recurso', 18.5, 0.2, 90.97, 15),
('SemillaSelecionadaBascica', 'semilla', 18.5, 0.2, 18.95, 150);





