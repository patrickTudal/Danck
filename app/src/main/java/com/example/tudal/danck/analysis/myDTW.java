package com.example.tudal.danck.analysis;

import fr.enseeiht.danck.voice_analyzer.DTWHelper;
import fr.enseeiht.danck.voice_analyzer.Field;

public class myDTW extends DTWHelper {

    @Override
    public float DTWDistance(Field unknown, Field known) {
        // Methode qui calcule le score de la DTW
        // entre 2 ensembles de MFCC
        myMFCCdistance op = new myMFCCdistance();
        int w0 = 1;
        int w1 = 2;
        int w2 = 1;
        float t1, t2, t3;
        int i = known.getLength() + 1;
        int j = unknown.getLength() + 1;
        float[][] result = new float[i][j];
        float elfamoso = 0;

        result[0][0] = 0;

        for (int colonne = 1; colonne < j; colonne++)
            result[0][colonne] = 99999;

        for (int ligne = 1; ligne < i; ligne++) {
            result[ligne][0] = 99999;
            for (int colonne = 1; colonne < j; colonne++) {
                elfamoso = op.distance(known.getMFCC(ligne - 1), unknown.getMFCC(colonne - 1));
                t1 = result[ligne - 1][colonne] + w0 * elfamoso;
                t2 = result[ligne - 1][colonne - 1] + w1 * elfamoso;
                t3 = result[ligne][colonne - 1] + w2 * elfamoso;
                if (t1 <= t2 && t1 <= t3) {
                    result[ligne][colonne] = t1;
                } else if (t2 <= t1 && t2 <= t3) {
                    result[ligne][colonne] = t2;
                } else {
                    result[ligne][colonne] = t3;
                }
            }
        }

        elfamoso = result[i - 1][j - 1] / ((i - 1) + (j - 1));

        return elfamoso;
    }

}
