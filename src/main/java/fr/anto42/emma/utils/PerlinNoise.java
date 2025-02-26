package fr.anto42.emma.utils;

import java.util.Random;

public class PerlinNoise {
    private final int[] permutationTable;

    public PerlinNoise(int seed) {
        permutationTable = new int[512];
        Random random = new Random(seed);

        // Créer une table de permutation aléatoire
        for (int i = 0; i < 256; i++) {
            permutationTable[i] = i;
        }

        // Mélanger la table
        for (int i = 0; i < 256; i++) {
            int swapWith = random.nextInt(256);
            int temp = permutationTable[i];
            permutationTable[i] = permutationTable[swapWith];
            permutationTable[swapWith] = temp;
        }

        // Dupliquer la table pour éviter les dépassements de tableau
        System.arraycopy(permutationTable, 0, permutationTable, 256, 256);
    }

    // Fonction de bruit Perlin 2D
    public double getNoise(double x, double y) {
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        // Calculer les coefficients d'interpolation
        double u = fade(xf);
        double v = fade(yf);

        // Calculer les index pour les permutations
        int aa = permutationTable[xi + permutationTable[yi]] % 12;
        int ab = permutationTable[xi + permutationTable[yi + 1]] % 12;
        int ba = permutationTable[xi + 1 + permutationTable[yi]] % 12;
        int bb = permutationTable[xi + 1 + permutationTable[yi + 1]] % 12;

        // Interpolation
        double x1 = lerp(grad(aa, xf, yf), grad(ba, xf - 1, yf), u);
        double x2 = lerp(grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1), u);

        return (lerp(x1, x2, v) + 1) / 2; // Normaliser entre 0 et 1
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10); // Fonction de lissage
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a); // Interpolation linéaire
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : (h == 12 || h == 14) ? x : 0;

        return (h & 1) == 0 ? u + v : u - v; // Calcul du gradient
    }
}
