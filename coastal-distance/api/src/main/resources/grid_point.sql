-- Clean up the contour of the generated grid
-- Remove grid points west of the Texas tip and south of the Mexican border (~20k points)
DELETE FROM grid_point WHERE lat < 25.767583 AND lon < -97.1595;
-- Remove grid points west of Tamps, Mexico and south of the last inward curve of the Mexican border (~36k points)
DELETE FROM grid_point WHERE lat < 27.317227 AND lon < -99.539810;
-- Remove grid points west of Jiminez, Mexico and south of the second-to-last inward curve of the Mexican border (~37k points)
DELETE FROM grid_point WHERE lat < 28.943701 AND lon < -100.652599;
-- Remove grid points west of Bosque, Mexico and south of the third-to-last inward curve of the Mexican border (~20,500 points)
DELETE FROM grid_point WHERE lat < 30.050820 AND lon < -104.707305;
-- Remove grid points west of Bosque, Mexico and south of the fourth-to-last inward curve of the Mexican border (~6k points)
DELETE FROM grid_point WHERE lat < 30.390397 AND lon < -104.859585;
-- Remove grid points west of Guadalupe, Mexico and south of the fifth-to-last inward curve of the Mexican border (~14k points)
DELETE FROM grid_point WHERE lat < 31.166943 AND lon < -105.774175;
-- Remove grid points west of Chihuaua, Mexico and south of the sixth-to-last inward curve of the Mexican border (~5500k points)
DELETE FROM grid_point WHERE lat < 31.481298 AND lon < -106.219796;
-- Remove grid points west and south of the sharp upward jut in the Mexican border near El Paso (~?k points)
DELETE FROM grid_point WHERE lat < 31.333350 AND lon < -108.208526;
-- Remove grid points west and south of the Mexican border near Nogales (~?k points)
DELETE FROM grid_point WHERE lat < 31.332130 AND lon < -111.074937;
-- Remove grid points west and south of the Mexican border near San Luis Rio Colorado (~?k points)
DELETE FROM grid_point WHERE lat < 31.942938 AND lon < -113.027407;
-- Remove grid points west and south of the Mexican border near Colonia Miguel Aleman (~?k points)
DELETE FROM grid_point WHERE lat < 32.494286 AND lon < -114.813748;


