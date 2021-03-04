package com.gitlab.hillel.dnepr.java.ee.common.utils;

import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class H2TypeUtils {
    public static Class<?> toJavaType(String h2DataTypeName) {
        Class<?> result = null;
        for (H2Types h2Type : H2Types.values()) {
            for (String h2TypeAlias : h2Type.typeAliases) {
                if (h2TypeAlias.equalsIgnoreCase(h2DataTypeName)) {
                    result = h2Type.getJavaTypes().getFirst();
                    break;
                }
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public static String toH2Type(Class<?> javaType) {
        String result = null;
        for (H2Types h2Type : H2Types.values()) {
            for (Class<?> enumJavaType : h2Type.getJavaTypes()) {
                if (enumJavaType == javaType) {
                    result = h2Type.getH2Type();
                    break;
                }
            }
            if (result != null) {
                break;
            }
        }
        return Objects.requireNonNull(result, "Type mapping is not found");
    }

    public static String toH2Type(Object javaObject) {
        return toH2Type(javaObject.getClass());
    }

    /**
     * Encapsulates H2 data types
     * https://h2database.com/html/datatypes.html#boolean_type
     */
    public enum H2Types {
        /**
         * Possible values: -2147483648 to 2147483647.
         * See also integer literal grammar. Mapped to java.lang.Integer.
         */
        INT(Arrays.asList(int.class, Integer.class), Arrays.asList("INTEGER", "INT", "MEDIUMINT", "INT4", "SIGNED")),

        /**
         * Possible values: TRUE, FALSE, and UNKNOWN (NULL).
         * See also boolean literal grammar. Mapped to java.lang.Boolean.
         */
        BOOLEAN(Arrays.asList(boolean.class, Boolean.class), Arrays.asList("BOOLEAN", "BIT", "BOOL")),

        /**
         * Possible values are: -128 to 127.
         * See also integer literal grammar. Mapped to java.lang.Byte.
         */
        TINYINT(Arrays.asList(byte.class, Byte.class), "TINYINT"),

        /**
         * Possible values: -32768 to 32767.
         * See also integer literal grammar. Mapped to java.lang.Short.
         * Example: SMALLINT
         */
        SMALLINT(Arrays.asList(short.class, Short.class), Arrays.asList("SMALLINT", "INT2", "YEAR")),

        /**
         * Possible values: -9223372036854775808 to 9223372036854775807.
         * See also long literal grammar. Mapped to java.lang.Long.
         * Example: BIGINT
         */
        BIGINT(Arrays.asList(long.class, Long.class), Arrays.asList("BIGINT", "INT8")),

        /**
         * Auto-Increment value. Possible values: -9223372036854775808 to 9223372036854775807. Used values are never re-used, even when the transaction is rolled back.
         * See also long literal grammar. Mapped to java.lang.Long.
         * Example: IDENTITY
         */
        IDENTITY(Arrays.asList(long.class, Long.class), "IDENTITY"),

        /**
         * Data type with fixed precision and scale. This data type is recommended for storing currency values.
         * See also numeric literal grammar. Mapped to java.math.BigDecimal.
         * Example: DECIMAL(20, 2)
         */
        DECIMAL(BigDecimal.class, Arrays.asList("DECIMAL", "NUMBER", "DEC", "NUMERIC")),

        /**
         * A floating point number. Should not be used to represent currency values, because of rounding problems. If precision value is specified for FLOAT type name, it should be from 25 to 53.
         * See also numeric literal grammar. Mapped to java.lang.Double.
         * Example: DOUBLE
         */
        DOUBLE(Arrays.asList(double.class, Double.class), Arrays.asList("DOUBLE", "FLOAT", "FLOAT8")),

        /**
         * A single precision floating point number. Should not be used to represent currency values, because of rounding problems. Precision value for FLOAT type name should be from 0 to 24.
         * See also numeric literal grammar. Mapped to java.lang.Float.
         * Example: REAL
         */
        REAL(Arrays.asList(float.class, Float.class), Arrays.asList("REAL", "FLOAT", "FLOAT4")),

        /**
         * The time data type. The format is hh:mm:ss[.nnnnnnnnn]. If fractional seconds precision is specified it should be from 0 to 9, 0 is default.
         * See also time literal grammar. Mapped to java.sql.Time. java.time.LocalTime is also supported and recommended on Java 8 and later versions. Use java.time.LocalTime or String instead of java.sql.Time when non-zero precision is needed. Cast from higher fractional seconds precision to lower fractional seconds precision performs round half up; if result of rounding is higher than maximum supported value 23:59:59.999999999 the value is rounded down instead. The CAST operation to TIMESTAMP and TIMESTAMP WITH TIME ZONE data types uses the CURRENT_DATE for date fields, comparison operations with values of these data types use the 1970-01-01 instead.
         * Example:
         * TIME
         * TIME(9)
         */
        TIME(LocalTime.class, "TIME"),

        /**
         * The time with time zone data type. If fractional seconds precision is specified it should be from 0 to 9, 0 is default.
         * See also time with time zone literal grammar. Mapped to java.time.OffsetTime on Java 8 and later versions. Cast from higher fractional seconds precision to lower fractional seconds precision performs round half up; if result of rounding is higher than maximum supported value 23:59:59.999999999 the value is rounded down instead. The CAST operation to TIMESTAMP and TIMESTAMP WITH TIME ZONE data types uses the CURRENT_DATE for date fields, comparison operations with values of these data types use the 1970-01-01 instead.
         * Example:
         * TIME WITH TIME ZONE
         * TIME(9) WITH TIME ZONE
         */
        TIME_WITH_TIME_ZONE(OffsetTime.class, "TIME WITH TIME ZONE"),

        /**
         * The date data type. The proleptic Gregorian calendar is used.
         * See also date literal grammar. Mapped to java.sql.Date, with the time set to 00:00:00 (or to the next possible time if midnight doesn't exist for the given date and time zone due to a daylight saving change). java.time.LocalDate is also supported and recommended on Java 8 and later versions.
         * If your time zone had LMT (local mean time) in the past and you use such old dates (depends on the time zone, usually 100 or more years ago), don't use java.sql.Date to read and write them.
         * If you deal with very old dates (before 1582-10-15) note that java.sql.Date uses a mixed Julian/Gregorian calendar, java.util.GregorianCalendar can be configured to proleptic Gregorian with setGregorianChange(new java.util.Date(Long.MIN_VALUE)) and used to read or write fields of dates.
         * Example:
         * DATE
         */
        DATE(LocalDate.class, "DATE"),

        /**
         * The timestamp data type. The proleptic Gregorian calendar is used. If fractional seconds precision is specified it should be from 0 to 9, 6 is default. Fractional seconds precision of SMALLDATETIME is always 0 and cannot be specified.
         * This data type holds the local date and time without time zone information. It cannot distinguish timestamps near transitions from DST to normal time. For absolute timestamps use the TIMESTAMP WITH TIME ZONE data type instead.
         * See also timestamp literal grammar. Mapped to java.sql.Timestamp (java.util.Date may be used too). java.time.LocalDateTime is also supported and recommended on Java 8 and later versions.
         * If your time zone had LMT (local mean time) in the past and you use such old dates (depends on the time zone, usually 100 or more years ago), don't use java.sql.Timestamp and java.util.Date to read and write them.
         * If you deal with very old dates (before 1582-10-15) note that java.sql.Timestamp and java.util.Date use a mixed Julian/Gregorian calendar, java.util.GregorianCalendar can be configured to proleptic Gregorian with setGregorianChange(new java.util.Date(Long.MIN_VALUE)) and used to read or write fields of timestamps.
         * Cast from higher fractional seconds precision to lower fractional seconds precision performs round half up.
         * Example:
         * TIMESTAMP
         * TIMESTAMP(9)
         */
        TIMESTAMP(Date.class, Arrays.asList("TIMESTAMP", "DATETIME", "SMALLDATETIME")),

        /**
         * The timestamp with time zone data type. The proleptic Gregorian calendar is used. If fractional seconds precision is specified it should be from 0 to 9, 6 is default.
         * See also timestamp with time zone literal grammar. Mapped to org.h2.api.TimestampWithTimeZone by default and can be optionally mapped to java.time.OffsetDateTime. java.time.ZonedDateTime and java.time.Instant are also supported on Java 8 and later versions.
         * Values of this data type are compared by UTC values. It means that 2010-01-01 10:00:00+01 is greater than 2010-01-01 11:00:00+03.
         * Conversion to TIMESTAMP uses time zone offset to get UTC time and converts it to local time using the system time zone. Conversion from TIMESTAMP does the same operations in reverse and sets time zone offset to offset of the system time zone. Cast from higher fractional seconds precision to lower fractional seconds precision performs round half up.
         * Example:
         * TIMESTAMP WITH TIME ZONE
         * TIMESTAMP(9) WITH TIME ZONE
         */
        TIMESTAMP_WITH_TIME_ZONE(ZonedDateTime.class, "TIMESTAMP WITH TIME ZONE"),

        /**
         * Represents a byte array. For very long arrays, use BLOB. The maximum size is 2 GB, but the whole object is kept in memory when using this data type. The precision is a size constraint; only the actual data is persisted. For large text data BLOB or CLOB should be used.
         * See also bytes literal grammar. Mapped to byte[].
         * Example:
         * BINARY(1000)
         */
        BINARY(Arrays.asList(byte[].class, Byte[].class), Arrays.asList("BINARY", "VARBINARY", "BINARY", "VARYING", "LONGVARBINARY", "RAW", "BYTEA")),

        /**
         * This type allows storing serialized Java objects. Internally, a byte array is used. Serialization and deserialization is done on the client side only. Deserialization is only done when getObject is called. Java operations cannot be executed inside the database engine for security reasons. Use PreparedStatement.setObject to store values.
         * Mapped to java.lang.Object (or any subclass).
         * Example:
         * OTHER
         */
        OTHER(Object.class, "OTHER"),

        /**
         * A Unicode String. Use two single quotes ('') to create a quote.
         * The maximum precision is Integer.MAX_VALUE. The precision is a size constraint; only the actual data is persisted.
         * The whole text is loaded into memory when using this data type. For large text data CLOB should be used; see there for details.
         * See also string literal grammar. Mapped to java.lang.String.
         * Example:
         * VARCHAR(255)
         */
        VARCHAR(String.class, Arrays.asList("VARCHAR", "CHARACTER VARYING", "LONGVARCHAR", "VARCHAR2", "NVARCHAR", "NVARCHAR2", "VARCHAR_CASESENSITIVE")),

        /**
         * Same as VARCHAR, but not case sensitive when comparing. Stored in mixed case.
         * The maximum precision is Integer.MAX_VALUE. The precision is a size constraint; only the actual data is persisted.
         * The whole text is loaded into memory when using this data type. For large text data CLOB should be used; see there for details.
         * See also string literal grammar. Mapped to java.lang.String.
         * Example:
         * VARCHAR_IGNORECASE
         */
        VARCHAR_IGNORECASE(String.class, "VARCHAR_IGNORECASE"),

        /**
         * A Unicode String. This type is supported for compatibility with other databases and older applications. The difference to VARCHAR is that trailing spaces are ignored and not persisted.
         * The maximum precision is Integer.MAX_VALUE. The precision is a size constraint; only the actual data is persisted.
         * The whole text is kept in memory when using this data type. For large text data CLOB should be used; see there for details.
         * See also string literal grammar. Mapped to java.lang.String.
         * Example:
         * CHAR(10)
         */
        CHAR(String.class, Arrays.asList("CHAR", "CHARACTER", "NCHAR")),

        /**
         * Like BINARY, but intended for very large values such as files or images. Unlike when using BINARY, large objects are not kept fully in-memory. Use PreparedStatement.setBinaryStream to store values. See also CLOB and Advanced / Large Objects.
         * Mapped to java.sql.Blob (java.io.InputStream is also supported).
         * Example:
         * BLOB
         * BLOB(10K)
         */
        BLOB(InputStream.class, Arrays.asList("BLOB", "BINARY", "LARGE OBJECT", "TINYBLOB", "MEDIUMBLOB", "LONGBLOB", "IMAGE", "OID")),

        /**
         * CLOB is like VARCHAR, but intended for very large values. Unlike when using VARCHAR, large CLOB objects are not kept fully in-memory; instead, they are streamed. CLOB should be used for documents and texts with arbitrary size such as XML or HTML documents, text files, or memo fields of unlimited size. Use PreparedStatement.setCharacterStream to store values. See also Advanced / Large Objects.
         * VARCHAR should be used for text with relatively short average size (for example shorter than 200 characters). Short CLOB values are stored inline, but there is an overhead compared to VARCHAR.
         * Precision, if any, should be specified in characters, CHARACTERS and OCTETS units have no effect in H2.
         * Mapped to java.sql.Clob (java.io.Reader is also supported).
         * Example:
         * CLOB
         * CLOB(10K)
         */
        CLOB(Reader.class, Arrays.asList("CLOB", "CHARACTER", "LARGE OBJECT", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT", "NTEXT", "NCLOB")),

        /**
         * Universally unique identifier. This is a 128 bit value. To store values, use PreparedStatement.setBytes, setString, or setObject(uuid) (where uuid is a java.util.UUID). ResultSet.getObject will return a java.util.UUID.
         * Please note that using an index on randomly generated data will result on poor performance once there are millions of rows in a table. The reason is that the cache behavior is very bad with randomly distributed data. This is a problem for any database system.
         * For details, see the documentation of java.util.UUID.
         * Example:
         * UUID
         */
        UUID(java.util.UUID.class, Arrays.asList("UUID")),

        /**
         * An array of values. Maximum cardinality, if any, specifies maximum allowed number of elements in the array.
         * See also array literal grammar. Mapped to java.lang.Object[] (arrays of any non-primitive type are also supported).
         * Use PreparedStatement.setArray(..) or PreparedStatement.setObject(.., new Object[] {..}) to store values, and ResultSet.getObject(..) or ResultSet.getArray(..) to retrieve the values.
         * Example:
         * ARRAY
         * ARRAY[10]
         */
        ARRAY(Object[].class, "ARRAY"),

        /**
         * A type with enumerated values. Mapped to java.lang.Integer.
         * The first provided value is mapped to 0, the second mapped to 1, and so on.
         * Duplicate and empty values are not permitted.
         * Example:
         * ENUM('clubs', 'diamonds', 'hearts', 'spades')
         */
        ENUM(Arrays.asList(int.class, Integer.class), "ENUM"),

        /**
         * A spatial geometry type. If additional constraints are not specified this type accepts all supported types of geometries. A constraint with required geometry type and dimension system can be set by specifying name of the type and dimension system. A whitespace between them is optional. 2D dimension system does not have a name and assumed if only a geometry type name is specified. POINT means 2D point, POINT Z or POINTZ means 3D point. GEOMETRY constraint means no restrictions on type or dimension system of geometry. A constraint with required spatial reference system identifier (SRID) can be set by specifying this identifier.
         * Mapped to org.locationtech.jts.geom.Geometry if JTS library is in classpath and to java.lang.String otherwise. May be represented in textual format using the WKT (well-known text) or EWKT (extended well-known text) format. Values are stored internally in EWKB (extended well-known binary) format. Only a subset of EWKB and EWKT features is supported. Supported objects are POINT, LINESTRING, POLYGON, MULTIPOINT, MULTILINESTRING, MULTIPOLYGON, and GEOMETRYCOLLECTION. Supported dimension systems are 2D (XY), Z (XYZ), M (XYM), and ZM (XYZM). SRID (spatial reference system identifier) is supported.
         * Use a quoted string containing a WKT/EWKT formatted string or PreparedStatement.setObject() to store values, and ResultSet.getObject(..) or ResultSet.getString(..) to retrieve the values.
         * Example:
         * GEOMETRY
         * GEOMETRY(POINT)
         * GEOMETRY(POINT Z)
         * GEOMETRY(POINT Z, 4326)
         * GEOMETRY(GEOMETRY, 4326)
         */
        GEOMETRY(String.class, "GEOMETRY"),

        /**
         * A RFC 8259-compliant JSON text.
         * See also json literal grammar. Mapped to byte[]. To set a JSON value with java.lang.String in a PreparedStatement use a FORMAT JSON data format (INSERT INTO TEST(ID, DATA) VALUES (?, ? FORMAT JSON)). Without the data format VARCHAR values are converted to a JSON string values.
         * Order of object members is preserved as is. Duplicate object member names are allowed.
         * Example:
         * JSON
         */
        JSON(String.class, "JSON"),

        /**
         * Interval data type. There are two classes of intervals. Year-month intervals can store years and months. Day-time intervals can store days, hours, minutes, and seconds. Year-month intervals are comparable only with another year-month intervals. Day-time intervals are comparable only with another day-time intervals.
         * Mapped to org.h2.api.Interval.
         * Example:
         * INTERVAL DAY TO SECOND
         */
        @Deprecated
        INTERVAL(Void.class, "IGNORED"),

        /**
         * Interval data type. If precision is specified it should be from 1 to 18, 2 is default.
         * See also year interval literal grammar. Mapped to org.h2.api.Interval. java.time.Period is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL YEAR
         */
        INTERVAL_YEAR(Period.class, "INTERVAL YEAR"),

        /**
         * Interval data type. If precision is specified it should be from 1 to 18, 2 is default.
         * See also month interval literal grammar. Mapped to org.h2.api.Interval. java.time.Period is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL MONTH
         */
        INTERVAL_MONTH(Period.class, "INTERVAL MONTH"),

        /**
         * Interval data type. If precision is specified it should be from 1 to 18, 2 is default.
         * See also day interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL DAY
         */
        INTERVAL_DAY(Duration.class, "INTERVAL DAY"),

        /**
         * Interval data type. If precision is specified it should be from 1 to 18, 2 is default.
         * See also hour interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL HOUR
         */
        INTERVAL_HOUR(Duration.class, "INTERVAL HOUR"),

        /**
         * Interval data type. If precision is specified it should be from 1 to 18, 2 is default.
         * See also minute interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL MINUTE
         */
        INTERVAL_MINUTE(Duration.class, "INTERVAL MINUTE"),

        /**
         * Interval data type. If precision is specified it should be from 1 to 18, 2 is default. If fractional seconds precision is specified it should be from 0 to 9, 6 is default.
         * See also second interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL SECOND
         */
        INTERVAL_SECOND(Duration.class, "INTERVAL SECOND"),

        /**
         * Interval data type. If leading field precision is specified it should be from 1 to 18, 2 is default.
         * See also year to month interval literal grammar. Mapped to org.h2.api.Interval. java.time.Period is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL YEAR TO MONTH
         */
        INTERVAL_YEAR_TO_MONTH(Period.class, "INTERVAL YEAR TO MONTH"),

        /**
         * Interval data type. If leading field precision is specified it should be from 1 to 18, 2 is default.
         * See also day to hour interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL DAY TO HOUR
         */
        INTERVAL_DAY_TO_HOUR(Duration.class, "INTERVAL DAY TO HOUR"),

        /**
         * Interval data type. If leading field precision is specified it should be from 1 to 18, 2 is default.
         * See also day to minute interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL DAY TO MINUTE
         */
        INTERVAL_DAY_TO_MINUTE(Duration.class, "INTERVAL DAY TO MINUTE"),

        /**
         * Interval data type. If leading field precision is specified it should be from 1 to 18, 2 is default. If fractional seconds precision is specified it should be from 0 to 9, 6 is default.
         * See also day to second interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL DAY TO SECOND
         */
        INTERVAL_DAY_TO_SECOND(Duration.class, "INTERVAL DAY TO SECOND"),

        /**
         * Interval data type. If leading field precision is specified it should be from 1 to 18, 2 is default.
         * See also hour to minute interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL HOUR TO MINUTE
         */
        INTERVAL_HOUR_TO_MINUTE(Duration.class, "INTERVAL HOUR TO MINUTE"),

        /**
         * Interval data type. If leading field precision is specified it should be from 1 to 18, 2 is default. If fractional seconds precision is specified it should be from 0 to 9, 6 is default.
         * See also hour to second interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL HOUR TO SECOND
         */
        INTERVAL_HOUR_TO_SECOND(Duration.class, "INTERVAL HOUR TO SECOND"),

        /**
         * Interval data type. If leading field precision is specified it should be from 1 to 18, 2 is default. If fractional seconds precision is specified it should be from 0 to 9, 6 is default.
         * See also minute to second interval literal grammar. Mapped to org.h2.api.Interval. java.time.Duration is also supported on Java 8 and later versions.
         * Example:
         * INTERVAL MINUTE TO SECOND
         */
        INTERVAL_MINUTE_TO_SECOND(Duration.class, "INTERVAL MINUTE TO SECOND");

        private final Deque<String> typeAliases = new ArrayDeque<>();
        private final Deque<Class<?>> javaTypes = new ArrayDeque<>();

        H2Types(List<Class<?>> javaTypes, List<String> typeAliases) {
            this.javaTypes.addAll(javaTypes);
            this.typeAliases.addAll(typeAliases);
        }

        H2Types(List<Class<?>> javaTypes, String typeAlias) {
            this.javaTypes.addAll(javaTypes);
            this.typeAliases.add(typeAlias);
        }

        H2Types(Class<?> javaType, List<String> typeAliases) {
            this.javaTypes.add(javaType);
            this.typeAliases.addAll(typeAliases);
        }

        H2Types(Class<?> javaType, String typeAlias) {
            this.javaTypes.add(javaType);
            this.typeAliases.add(typeAlias);
        }

        public Deque<Class<?>> getJavaTypes() {
            return this.javaTypes;
        }

        public String getH2Type() {
            return this.typeAliases.getFirst();
        }
    }
}
