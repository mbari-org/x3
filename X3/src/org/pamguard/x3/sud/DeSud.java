package org.pamguard.x3.sud;

import java.io.*;

/**
 * Ad hoc program to decompress SUD file.
 * It is essentially as `SudAudioInputStream.main` but with ability
 * to pass some arguments from the command line.
 */
public class DeSud {

  static class DeSudParams {
    final String filePath;
    final SudParams sudParams = new SudParams();

    DeSudParams(String[] args) {
      if (args.length == 0) {
        usage();
      }
      this.filePath = args[0];
      for (int i = 1; i < args.length; i++) {
          switch (args[i]) {
              case "-nz":
                  this.sudParams.zeroPad = false;
                  break;
              case "-nw":
                  this.sudParams.setSaveWav(false);
                  break;
              case "-v":
                  this.sudParams.setVerbose(true);
                  break;
              case "-o":
                  this.sudParams.saveFolder = args[++i];
                  break;
              case "-h":
                  usage();
                  break;
          }
      }
    }

    static void usage() {
      System.out.println("Usage: java -cp X3-x.y.z.jar org.pamguard.x3.sud.DeSud <sud-file> [options]");
      System.out.println("Options:");
      System.out.println("  -nz      - Do not zero pad");
      System.out.println("  -nw      - Do not save wav file");
      System.out.println("  -v       - verbose");
      System.out.println("  -o <dir> - output folder");
      System.out.println("  -h       - help");
      System.exit(0);
    }
  }

  //
  // Basically a copy of SudAudioInputStream.main, but adjusted to handle
  // the parameters from the command line.
  //
	public static void main(String[] args) {
    DeSudParams deSudParams = new DeSudParams(args);

		long time0 = System.currentTimeMillis();
		String filePath = deSudParams.filePath;

    SudAudioInputStream sudAudioInputStream = null;
		File file = new File(filePath);

		File sudMapFileName = new File(file.getAbsoluteFile() + "x");
		SudFileMap sudFileMap = null;
		if (sudMapFileName.exists()) {
			SudFileMap loadedFileMap ;
			try {
				loadedFileMap = SudAudioInputStream.loadSudMap(sudMapFileName);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println("Could not open .sudx file map");
				loadedFileMap = null;
			}
		}

		SudParams sudParams = deSudParams.sudParams;

		boolean verbose = false; // true to print more stuff.

		try {
			sudAudioInputStream = SudAudioInputStream.openInputStream(new File(filePath), sudParams, verbose);

			long time1 = System.currentTimeMillis();

			System.out.println("Time to create file map: " + (time1 - time0) + " sample rate: " + sudAudioInputStream.getFormat().getSampleRate() + " " + sudAudioInputStream.getSudMap().clickDetSampleRate);

			System.out.println("sudAudioInputStream.available() 1: " + sudAudioInputStream.available());

			sudAudioInputStream.skip(500000 * 0);

			long time2 = System.currentTimeMillis();

			System.out.println("Time to skip 1: " + (time2 - time1));


			time1 = System.currentTimeMillis();

			sudAudioInputStream.skip(500000 * 0);

			time2 = System.currentTimeMillis();

			System.out.println("Time to skip 2: " + (time2 - time1));

			System.out.println("sudAudioInputStream.available() 2: " + sudAudioInputStream.available());

			while (sudAudioInputStream.available() > 0) {
				sudAudioInputStream.read();
			}

			sudAudioInputStream.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		long time3 = System.currentTimeMillis();

		System.out.println("Total processing time: " + (time3 - time0));

		System.out.println(sudAudioInputStream.getSudMap().xmlMetaData);
	}
}
