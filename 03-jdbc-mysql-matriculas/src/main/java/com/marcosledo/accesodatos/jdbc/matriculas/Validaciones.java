package com.marcosledo.accesodatos.jdbc.matriculas;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Marcos Ledo Martinez
 */

public class Validaciones {

    private static final Pattern patronEmail
            = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern patronFecha
            = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$"); // dd/mm/yyyy

    private static final String formatoFecha = "dd/MM/yyyy";

    public static boolean emailValido(String email) {
        if (email == null) {
            return false;
        }

        email = email.trim();
        Matcher m = patronEmail.matcher(email);
        return m.matches();
    }

    public static Date fechaNacimientoASqlDate(String fechaTexto, int edadMinima) {
        if (fechaTexto == null) {
            return null;
        }

        fechaTexto = fechaTexto.trim();

        if (!patronFecha.matcher(fechaTexto).matches()) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatoFecha);
        sdf.setLenient(false);

        java.util.Date nacUtil;
        try {
            nacUtil = sdf.parse(fechaTexto);
        } catch (ParseException e) {
            return null;
        }

        Calendar hoy = Calendar.getInstance();
        Calendar nac = Calendar.getInstance();
        nac.setTime(nacUtil);

        if (nac.after(hoy)) {
            return null;
        }

        int edad = hoy.get(Calendar.YEAR) - nac.get(Calendar.YEAR);

        int mesHoy = hoy.get(Calendar.MONTH);
        int mesNac = nac.get(Calendar.MONTH);
        int diaHoy = hoy.get(Calendar.DAY_OF_MONTH);
        int diaNac = nac.get(Calendar.DAY_OF_MONTH);

        if (mesHoy < mesNac || (mesHoy == mesNac && diaHoy < diaNac)) {
            edad--;
        }

        if (edad < edadMinima) {
            return null;
        }

        return new Date(nacUtil.getTime());
    }
}
