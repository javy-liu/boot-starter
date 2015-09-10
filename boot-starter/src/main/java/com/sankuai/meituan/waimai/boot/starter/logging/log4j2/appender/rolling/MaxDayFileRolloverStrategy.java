package com.sankuai.meituan.waimai.boot.starter.logging.log4j2.appender.rolling;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.*;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.FileRenameAction;
import org.apache.logging.log4j.core.appender.rolling.action.GzCompressAction;
import org.apache.logging.log4j.core.appender.rolling.action.ZipCompressAction;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.pattern.ArrayPatternConverter;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.logging.log4j.status.StatusLogger;
import org.joda.time.DateTime;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Deflater;

/**
 * 最大保存文件数策略
 *
 * @author liuzhenyuan
 * @version Last modified 15/9/15
 */
@Plugin(name = "MaxDayFileRolloverStrategy", category = "Core", printObject = true)
public class MaxDayFileRolloverStrategy implements RolloverStrategy {
    private static final String EXT_ZIP = ".zip";
    private static final String EXT_GZIP = ".gz";

    /**
     * Allow subclasses access to the status logger without creating another instance.
     */
    protected static final Logger LOGGER = StatusLogger.getLogger();

    private static final int MAX_FILE_NUM = 10;

    /**
     * 创建MaxFileRolloverStrategy
     * @param max 最大文件数
     * @param compressionLevelStr 压缩级别 0 (less) - 9 (more); 只支持ZIP文件.
     * @param config The Configuration.
     * @return A DefaultRolloverStrategy.
     */
    @PluginFactory
    public static MaxDayFileRolloverStrategy createStrategy(
            @PluginAttribute("max") final String max,
            @PluginAttribute("compressionLevel") final String compressionLevelStr,
            @PluginConfiguration final Configuration config) {
        int maxNum = MAX_FILE_NUM;
        if (max != null) {
            maxNum = Integer.parseInt(max);
            if (maxNum < 1) {
                LOGGER.error("File number too small. Limited to bigger than 0");
                maxNum = MAX_FILE_NUM;
            }
        }
        // TODO 检测当前文件数量
        final int compressionLevel = Integers.parseInt(compressionLevelStr, Deflater.DEFAULT_COMPRESSION);
        return new MaxDayFileRolloverStrategy(maxNum, compressionLevel, config.getStrSubstitutor());
    }

    /**
     * 最大文件数量
     */
    @Getter
    private final int maxNum;

    /**
     * 当前文件数量
     */
    @Getter
    @Setter
    private int currentNum = 0;

    @Getter
    @Setter
    private String fileName;
    @Getter
    @Setter
    private String filePattern;

    private final StrSubstitutor subst;
    @Getter
    private final int compressionLevel;

    /**
     * 构造函数
     *
     * @param maxNum 最大文件数
     * @param compressionLevel
     * @param subst
     */
    protected MaxDayFileRolloverStrategy(final int maxNum, final int compressionLevel, final StrSubstitutor subst) {
        this.maxNum = maxNum;
        this.compressionLevel = compressionLevel;
        this.subst = subst;
    }

    /**
     * 清除文件
     *
     * @param currentNum
     * @param manager
     * @return
     */
    private int purge(final int maxFileNum, final boolean cleanAll, final RollingFileManager manager) {
        if (currentNum >= maxNum) {

        }

        // 检测是否有该删除文件
        PatternProcessor processor = manager.getPatternProcessor();


        DateTime now = DateTime.now();
        DateTime oldest = now.minusDays(maxNum);

        int suffixLength = 0;

        final StringBuilder nowBuf = new StringBuilder();
        final StringBuilder oldestBuf = new StringBuilder();

//        PatternProcessor processor = manager.getPatternProcessor();
//            processor.formatFileName(subst, buf, i);
        processor.formatFileName(nowBuf, new Date(now.getMillis()));
        long nowTime = manager.getFileTime();



        processor.formatFileName(oldestBuf, new Date(oldest.getMillis()));

        String highFilename = subst.replace(nowBuf);

        String lowFilename = subst.replace(oldestBuf);

        if (highFilename.endsWith(EXT_GZIP)) {
            suffixLength = EXT_GZIP.length();
        } else if (highFilename.endsWith(EXT_ZIP)) {
            suffixLength = EXT_ZIP.length();
        }


        return 0;
    }


    private int purgeOldestFiles() {

        return 0;
    }

    /**
     * Purge and rename old log files in preparation for rollover. The oldest file will have the smallest index,
     * the newest the highest.
     *
     * @param lowIndex  low index
     * @param highIndex high index.  Log file associated with high index will be deleted if needed.
     * @param manager The RollingFileManager
     * @return true if purge was successful and rollover should be attempted.
     */
    private int purgeAscending(final int lowIndex, final int highIndex, final RollingFileManager manager) {
        int suffixLength = 0;

        final List<FileRenameAction> renames = new ArrayList<FileRenameAction>();
        final StringBuilder buf = new StringBuilder();

        // LOG4J2-531: directory scan & rollover must use same format
        manager.getPatternProcessor().formatFileName(subst, buf, highIndex);

        String highFilename = subst.replace(buf);

        if (highFilename.endsWith(EXT_GZIP)) {
            suffixLength = EXT_GZIP.length();
        } else if (highFilename.endsWith(EXT_ZIP)) {
            suffixLength = EXT_ZIP.length();
        }

        int maxIndex = 0;

        for (int i = highIndex; i >= lowIndex; i--) {
            File toRename = new File(highFilename);
            if (i == highIndex && toRename.exists()) {
                maxIndex = highIndex;
            } else if (maxIndex == 0 && toRename.exists()) {
                maxIndex = i + 1;
                break;
            }

            boolean isBase = false;

            if (suffixLength > 0) {
                final File toRenameBase =
                        new File(highFilename.substring(0, highFilename.length() - suffixLength));

                if (toRename.exists()) {
                    if (toRenameBase.exists()) {
                        LOGGER.debug("DefaultRolloverStrategy.purgeAscending deleting {} base of {}.", //
                                toRenameBase, toRename);
                        toRenameBase.delete();
                    }
                } else {
                    toRename = toRenameBase;
                    isBase = true;
                }
            }

            if (toRename.exists()) {
                //
                //    if at lower index and then all slots full
                //        attempt to delete last file
                //        if that fails then abandon purge
                if (i == lowIndex) {
                    LOGGER.debug("DefaultRolloverStrategy.purgeAscending deleting {} at low index {}: all slots full.", //
                            toRename, i);
                    if (!toRename.delete()) {
                        return -1;
                    }

                    break;
                }

                //
                //   if intermediate index
                //     add a rename action to the list
                buf.setLength(0);
                // LOG4J2-531: directory scan & rollover must use same format
                manager.getPatternProcessor().formatFileName(subst, buf, i - 1);

                final String lowFilename = subst.replace(buf);
                String renameTo = lowFilename;

                if (isBase) {
                    renameTo = lowFilename.substring(0, lowFilename.length() - suffixLength);
                }

                renames.add(new FileRenameAction(toRename, new File(renameTo), true));
                highFilename = lowFilename;
            } else {
                buf.setLength(0);
                // LOG4J2-531: directory scan & rollover must use same format
                manager.getPatternProcessor().formatFileName(subst, buf, i - 1);

                highFilename = subst.replace(buf);
            }
        }
        if (maxIndex == 0) {
            maxIndex = lowIndex;
        }

        //
        //   work renames backwards
        //
        for (int i = renames.size() - 1; i >= 0; i--) {
            final Action action = renames.get(i);
            try {
                LOGGER.debug("DefaultRolloverStrategy.purgeAscending executing {} of {}: {}", //
                        i, renames.size(), action);
                if (!action.execute()) {
                    return -1;
                }
            } catch (final Exception ex) {
                LOGGER.warn("Exception during purge in RollingFileAppender", ex);
                return -1;
            }
        }
        return maxIndex;
    }

    /**
     * Purge and rename old log files in preparation for rollover. The newest file will have the smallest index, the
     * oldest will have the highest.
     *
     * @param lowIndex  low index
     * @param highIndex high index.  Log file associated with high index will be deleted if needed.
     * @param manager The RollingFileManager
     * @return true if purge was successful and rollover should be attempted.
     */
    private int purgeDescending(final int lowIndex, final int highIndex, final RollingFileManager manager) {
        int suffixLength = 0;

        final List<FileRenameAction> renames = new ArrayList<FileRenameAction>();
        final StringBuilder buf = new StringBuilder();

        // LOG4J2-531: directory scan & rollover must use same format
        manager.getPatternProcessor().formatFileName(subst, buf, lowIndex);

        String lowFilename = subst.replace(buf);

        if (lowFilename.endsWith(EXT_GZIP)) {
            suffixLength = EXT_GZIP.length();
        } else if (lowFilename.endsWith(EXT_ZIP)) {
            suffixLength = EXT_ZIP.length();
        }

        for (int i = lowIndex; i <= highIndex; i++) {
            File toRename = new File(lowFilename);
            boolean isBase = false;

            if (suffixLength > 0) {
                final File toRenameBase =
                        new File(lowFilename.substring(0, lowFilename.length() - suffixLength));

                if (toRename.exists()) {
                    if (toRenameBase.exists()) {
                        LOGGER.debug("DefaultRolloverStrategy.purgeDescending deleting {} base of {}.", //
                                toRenameBase, toRename);
                        toRenameBase.delete();
                    }
                } else {
                    toRename = toRenameBase;
                    isBase = true;
                }
            }

            if (toRename.exists()) {
                //
                //    if at upper index then
                //        attempt to delete last file
                //        if that fails then abandon purge
                if (i == highIndex) {
                    LOGGER.debug("DefaultRolloverStrategy.purgeDescending deleting {} at high index {}: all slots full.", //
                            toRename, i);
                    if (!toRename.delete()) {
                        return -1;
                    }

                    break;
                }

                //
                //   if intermediate index
                //     add a rename action to the list
                buf.setLength(0);
                // LOG4J2-531: directory scan & rollover must use same format
                manager.getPatternProcessor().formatFileName(subst, buf, i + 1);

                final String highFilename = subst.replace(buf);
                String renameTo = highFilename;

                if (isBase) {
                    renameTo = highFilename.substring(0, highFilename.length() - suffixLength);
                }

                renames.add(new FileRenameAction(toRename, new File(renameTo), true));
                lowFilename = highFilename;
            } else {
                break;
            }
        }

        //
        //   work renames backwards
        //
        for (int i = renames.size() - 1; i >= 0; i--) {
            final Action action = renames.get(i);
            try {
                LOGGER.debug("DefaultRolloverStrategy.purgeDescending executing {} of {}: {}", //
                        i, renames.size(), action);
                if (!action.execute()) {
                    return -1;
                }
            } catch (final Exception ex) {
                LOGGER.warn("Exception during purge in RollingFileAppender", ex);
                return -1;
            }
        }

        return lowIndex;
    }

    /**
     * Perform the rollover.
     * @param manager The RollingFileManager name for current active log file.
     * @return A RolloverDescription.
     * @throws SecurityException if an error occurs.
     */
    @Override
    public RolloverDescription rollover(final RollingFileManager manager) throws SecurityException {

        final long start = System.nanoTime();


        // 清除文件
        final int fileIndex = purge(currentNum, true, manager);
        if (fileIndex < 0) {
            return null;
        }
        if (LOGGER.isTraceEnabled()) {
            final double duration = (System.nanoTime() - start) / (1000.0 * 1000.0 * 1000.0);
            LOGGER.trace("DefaultRolloverStrategy.purge() took {} seconds", duration);
        }
        final StringBuilder buf = new StringBuilder(255);
        manager.getPatternProcessor().formatFileName(subst, buf, fileIndex);
        final String currentFileName = manager.getFileName();

        String renameTo = buf.toString();
        final String compressedName = renameTo;
        Action compressAction = null;

        if (renameTo.endsWith(EXT_GZIP)) {
            renameTo = renameTo.substring(0, renameTo.length() - EXT_GZIP.length());
            compressAction = new GzCompressAction(new File(renameTo), new File(compressedName), true);
        } else if (renameTo.endsWith(EXT_ZIP)) {
            renameTo = renameTo.substring(0, renameTo.length() - EXT_ZIP.length());
            compressAction = new ZipCompressAction(new File(renameTo), new File(compressedName), true,
                    compressionLevel);
        }

        final FileRenameAction renameAction =
                new FileRenameAction(new File(currentFileName), new File(renameTo), false);

        return new RolloverDescriptionImpl(currentFileName, false, renameAction, compressAction);
    }

    @Override
    public String toString() {
        return "MaxDayFileRolloverStrategy(currentNum=" + currentNum + ", maxNum=" + maxNum + ')';
    }
}
