/**
 * Utils.java created 09.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;

import lombok.extern.java.Log;

/**
 * Utils created 09.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
@Log
public class AUtils {
	public static final String DEFAULT_ERROR = "No Data";
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	
	private static final String TIME_PATTERN = "HH:mm:ss";
	private static final String DATE_PATTERN = "dd.MM.yyyy";
	
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	
	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN + " " + TIME_PATTERN);

	public static <T> T nvl(Optional<T> possibleValue, T defaultValue) {
		return possibleValue.orElse(defaultValue);
	}

	public static <T> T nvl(T possibleValue, T defaultValue) {
		return possibleValue == null? defaultValue: possibleValue;
	}
	
	public static boolean hasValue(String s) {
		return s != null && s.length() > 0;
	}
	
	public static boolean hasValue(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}
	
	/**
	 * @param clasz
	 * @return
	 * since 07.02.2024
	 */
	public static String[] getDeclaredPropertyNamesArray(Class<?> clasz) {
		var fieldList = getDeclaredPropertyNamesList(clasz);
		return (String[]) fieldList.toArray(new String[fieldList.size()]);
	}
	
	public static List<String> getDeclaredPropertyNamesList(Class<?> clasz) {
		var fieldList = new ArrayList<String>();
		for( Field field: clasz.getDeclaredFields()) {
			fieldList.add(field.getName());
			log.info("Declared Field: " + field.getName() + " class: " + field.getType().getName());
		}
		return fieldList;
	}

	public static String[] getAllPropertyNamesArray(Class<?> clasz) {
		var fieldList = getDeclaredPropertyNamesList(clasz);
			fieldList.addAll(getDeclaredPropertyNamesList(clasz.getSuperclass()));
		return (String[]) fieldList.toArray(new String[fieldList.size()]);
	}
	

    public static String readStaticFile(String filePath) {
    	
        final ClassPathResource resource = new ClassPathResource(filePath);
        byte[] bytes;
		try {
			bytes = Files.readAllBytes(Paths.get(resource.getURI()));
		} catch (IOException e) {
			log.warning(e.getLocalizedMessage());
			bytes = DEFAULT_ERROR.getBytes(DEFAULT_CHARSET);
		}
        return new String(bytes, DEFAULT_CHARSET);
    }

    public static <K extends Comparable<? super K>, V > Map<K, V> sortByKeys(Map<K, V> map) {
        List<Map.Entry<K, V>> sortedEntries = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : sortedEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
    
    /**
     * Liefert alle Klassen eines packages
     * 
     * @param String packageName Der Packagename
     * 
     * @return List<Class<?>> Die Klassen
     * 
     * anst 13.02.2024
     */
    public static List<Class<?>> getClassesInPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource != null) {
            File directory = new File(resource.getFile());

            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            try {
                                Class<?> clazz = Class.forName(className);
                                classes.add(clazz);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        return classes;
    }
    
    public static LocalDateTime LocalDateTimeofMillis(long milliseconds) {
    	 Instant instant = Instant.ofEpochMilli(milliseconds);

         // Konvertiere die Instant-Instanz in LocalDateTime
         return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
    
    public static String timeOfMillisString(long millis) {
    	DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN + " " + TIME_PATTERN);
    	return DATETIME_FORMATTER.format(LocalDateTimeofMillis(millis));
    }
    
    public static Map<String, Object> getAllGetters(Object obj) {
        Map<String, Object> result = new HashMap<>();

        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (isGetter(method)) {
                try {
                    String propertyName = getPropertyNameFromGetter(method.getName());
                    Object value = method.invoke(obj);
                    result.put(propertyName, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.info(e.getLocalizedMessage());
                }
            }
        }

        return result;
    }

    private static boolean isGetter(Method method) {
        return method.getName().startsWith("get") && method.getParameterCount() == 0
                && !method.getReturnType().equals(void.class);
    }

    private static String getPropertyNameFromGetter(String methodName) {
        // Beispiel: getUserName -> userName
        return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
    }
}
