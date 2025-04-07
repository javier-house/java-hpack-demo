package org.eu.liuhw.hpack.filter;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictTrainer;
import com.github.luben.zstd.ZstdException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author JavierHouse
 */
public class ZstdDictGenerator {

    /**
     * 将重复的数据压缩100次进行测试
     */
    public static byte[] demo(byte[] bytes) {
        int sampleCount = 50;

        int dictSize = bytes.length * (sampleCount);


        final ZstdDictTrainer trainer = new ZstdDictTrainer(1024 * 1024, 32 * 1024);
        for (int i = 0; i < (sampleCount*100); i++) {
            // 3. 添加训练样本
            if (!trainer.addSample(bytes)) {
                System.err.println("样本过大，无法添加到训练器");
            }
        }

        return trainer.trainSamples();
    }


    public static void main(String[] args) {
        // 1. 准备训练样本数据（实际场景应使用真实业务数据）
        byte[][] samples = {
                "This is a sample text for dictionary training".getBytes(),
                "Zstandard (zstd) is a fast lossless compression algorithm".getBytes(),
                "Generating a dictionary improves compression ratio".getBytes(),
                "Repeated patterns will be captured by the dictionary".getBytes()
        };
        byte[] samples1 = "This is a sample text for dictionary training".getBytes();

        // 2. 创建字典训练器
        int dictSize = samples1.length * 50; // 字典大小建议 100KB-1MB
        ZstdDictTrainer trainer = new ZstdDictTrainer(dictSize, samples1.length);
        for (int i = 0; i < 50; i++) {

            // 3. 添加训练样本
            /*for (byte[] sample : samples) {
                if (!trainer.addSample(sample)) {
                    System.err.println("样本过大，无法添加到训练器");
                    return;
                }
            }*/
            if (!trainer.addSample(samples1)) {
                System.err.println("样本过大，无法添加到训练器");
                return;
            }
        }
        try {
            // 4. 生成最终字典
            byte[] dictionary = trainer.trainSamples();

            // 5. 保存字典到文件（可选）
            //Files.write(Paths.get("zstd-dictionary.dict"), dictionary);
            //System.out.println("字典生成成功，大小: " + dictionary.length + " bytes");

            // 6. 使用字典进行压缩/解压演示
            byte[] dataToCompress = samples[0];
            byte[] compressed = compressWithDict(dataToCompress, dictionary);
            byte[] decompressed = decompressWithDict(compressed, dictionary);

            System.out.println("原始数据大小: " + dataToCompress.length);
            System.out.println("压缩后大小: " + compressed.length);
            System.out.println("解压验证结果: " +
                    new String(decompressed).equals(new String(dataToCompress)));

        } catch (ZstdException e) {
            e.printStackTrace();
        }
    }

    private static byte[] compressWithDict(byte[] data, byte[] dict) {
        return Zstd.compressUsingDict(data, dict, 3);
    }

    private static byte[] decompressWithDict(byte[] compressed, byte[] dict) {
        long decompressedSize = Zstd.decompressedSize(compressed);
        byte[] result = new byte[(int) decompressedSize];
        Zstd.decompressUsingDict(result, compressed, dict);
        return result;
    }


    public class CustomGZIPOutputStream extends GZIPOutputStream {
        // 自定义构造函数，允许传入压缩级别
        public CustomGZIPOutputStream(OutputStream out, int level) throws IOException {
            super(out);
            def.setLevel(level);  // 设置压缩级别
        }
    }

}
