package ai.transpotest;

import model.BitBoard;

public class Zobrist {

    private static final long[][] zobristTable = {{ 0xc3ab7b1c3acbd8abL, 0xe856d7cd9a0f8c74L, 0x9be0668a7b316aabL, 0xa7ff3b218a29965bL, 0x3a3f91b6393909caL, 0xf29fc4f70c3ce62eL, 0x80abdf502be30e90L, 0x8ff3cfa5b916ad71L, 0x15725e85116763efL, 0x897cefccabf327fL, 0x378a2e7eaa81fe73L, 0x54a0c9696efebce2L, 0x2f0963173b312b7L, 0x37cfbf51405472e5L, 0x594854540ea7bf1aL, 0xb589a4becc04fc68L, 0xfcf6babf0171cb3L, 0xee22d1456feffef2L, 0xeb2eef8f4aaaeb81L, 0x6970a1dee38c70cL, 0xf7dc94a6c54a4de7L, 0x51b58a91f8bfb2ccL, 0x9afe09f675904756L, 0x505aae9ea02f31afL, 0x383a5e19ed9ef24bL, 0xabc723d50b386dacL, 0xece5f4d23a1ba17cL, 0x93bec4817fc8f185L, 0xdcb9d0c0312f604dL, 0xd817810c8ac3e05fL, 0x180f356fe12a8689L, 0x3d02fba5b85e9295L, 0xbba4e597ac21534dL, 0x60e3ba1eb0e9abc8L, 0x8e8381a148c2c347L, 0xac291214d8ccfe3aL, 0xd7eaca64870a1620L, 0x2dbb3351880dcf5cL, 0x3589cc3a1c7eb736L, 0x9b32f476231c51a5L, 0xede0362e96bd7d8eL, 0x71af45d47d1e67a4L, 0x23c76194cf417709L, 0x14d46e98f0bb0458L, 0xc305cdc03811b2d4L, 0xd39f783318cabcbfL, 0x52b8140f295cbf25L, 0xa07a6bef6b875eacL, 0xad8698280361ce23L, 0xa1a446e3aa1da0ddL, 0x114f7887b992b625L, 0x42c9c80ee921404cL, 0xec608339e2d9411fL, 0x3eda7cbf4d951ec5L, 0x2dd532e61a9d7f58L, 0xf3f7af32ab3e36b8L, 0xd568f80cadd21160L, 0xe1111b8045117850L, 0xbd4e5291ca269d41L, 0x379d8eb7a4f9e3c3L, 0x929f7f7aaacfe2f4L, 0x221bd448063574ebL, 0x1f05bef841582416L, 0x93ea95a22a5cb416L },
            { 0x248076a540e1d2dbL, 0xdecc101abe120019L, 0x5b05b40c1a71bc42L, 0xc054c5151c92e5abL, 0xa349d36cb075218aL, 0x5bd288604a3717efL, 0x42944ec0520b74e4L, 0x862b90dd3a8dde52L, 0x4563f8f9e2e2ad69L, 0xaf0b6f63d0840b97L, 0xca05298f1df0664eL, 0x77ae0fe6bb09d22fL, 0x85609c383f413d55L, 0xf9f728d7305109cdL, 0xeb7208f666bb2486L, 0xd486bcc63a31ba3aL, 0xde41b854fa469b1fL, 0x1861924c80cbff65L, 0x22c97f4ac9e01c8L, 0x2ccec62f525b9abdL, 0xf8fa934486417e0aL, 0x479dbbdb1a2c5c3L, 0xf6fb349203012102L, 0x95faef7cec9719daL, 0xd25cd23f9f764551L, 0x57dcf0062c54a161L, 0xc572cc55d9e05856L, 0x8f54012d73086603L, 0x54e39e3ef068829fL, 0xff546d6605c5104dL, 0xdaffc8ce7f12e39bL, 0x5dcc96294496c80L, 0x1e0ed88270db5495L, 0x6d4c2e00e8e9e8a8L, 0x90700c0a9ce6e6ddL, 0x41c7ac64344b5fecL, 0xab405f0c71929147L, 0x5540606064aec728L, 0x870ffd227ebac0d9L, 0xe9ddc51a7c728eb8L, 0xf64d6124be1ec0bfL, 0x40a62feeb067cd96L, 0x2aca4ac8f7e2d82L, 0xb3799592b9e689a7L, 0x9e35c3d75d3de779L, 0x1c10fd7d4b6db151L, 0x720cd9495945387fL, 0xc7f74781de509a50L, 0x14b396723f9b20e5L, 0x319627c015bd19caL, 0x37b37c164856ffaaL, 0x62bc7cbd048289a0L, 0xed9497089bf709e9L, 0xe3b163cea0b34f06L, 0x6cfcc53997624592L, 0xad39afbd4fbfb655L, 0x590fc6f2cf02fb5cL, 0xe22ba5a58042381fL, 0xc9e8a05e15c1d3faL, 0x68872420d9a27b84L, 0x6890f4a5479148cL, 0x80c698a55635a9c3L, 0x1424192eebfaae58L, 0x867f672b27d7e713L },
            { 0x2974f56ed63b15b9L, 0x4daea8112a6404b3L, 0x7823d7de1dde1457L, 0x6e796040d02bc040L, 0x1e4e5f845d820a97L, 0x3f5979263e42446L, 0xf761ada14cf95455L, 0x4c03f16c869ede18L, 0x39d068f06968c4d0L, 0x7c97abb360a29ce3L, 0xa70f3130293d6de5L, 0x704527ddce81ce01L, 0xa0e646df0d5b2680L, 0x4ecd626639f04a8aL, 0xd2cf6b481108844fL, 0x80defc64eccdca31L, 0xa4033cc041343cb7L, 0x66abbb7cbde42f4cL, 0x3749932b9cfb210bL, 0xe97ba3f887360034L, 0xfcd4a0c53c1529fbL, 0xf755113206aaa1ceL, 0x70a6db1f3743464aL, 0xc763703290ab2769L, 0x797ff0660a3ee0c3L, 0xd43c80a0621bcbcbL, 0x694d4ceb6947516dL, 0x50be3fd2c14b6786L, 0x14def4dd8d78adfdL, 0x6b3de2476d33788fL, 0xd69b65d3ddaad8bL, 0x8b627f38dcf88f10L, 0x376bf7acbecd9389L, 0x4f8c528c6044337cL, 0xc81aa5c5f16655f0L, 0x153b8ec790e99e4L, 0xe0d0071457aa72bbL, 0x83b94a3589ffb689L, 0x574e767b2caf2898L, 0x352b616c6f487b1L, 0xc6454a9bffa706ddL, 0x775646f2e0aea8beL, 0x53a66d56322408b8L, 0xf4212f072681827bL, 0x63f678ebb83afaaL, 0xfaf303d16231acb9L, 0x61f0e533b8ddd136L, 0x95b0bade870551aL, 0xee0bf57ca1260bd5L, 0x437a41c4b4a5bf2cL, 0x640c8b74abbb172cL, 0xe9073422b65b31b9L, 0x725d02b8c39e6380L, 0x742f20271ac5e55cL, 0x2746ac7ea641bc69L, 0x8d41f11f8af9c857L, 0x67efa8d9e17fe2c5L, 0xccd053458642ba4L, 0xfe86903ad43051efL, 0xe1a2dc83029bad49L, 0x3dc0b13a9f15a431L, 0x78c168a9493f3f0L, 0x6ed7e696cbe94a76L, 0xa04061cd11190fb6L },
            { 0xf6814979fd3e8b44L, 0x690c41b0a05f9442L, 0x952644021721fce8L, 0xe72cea5d7b3c01aL, 0x633dc7fc4cf9eef2L, 0x8268d48d4a234531L, 0x343badf2607f76e3L, 0x5bb8f3576aa286c3L, 0x2d68806816ef1424L, 0x2bf6a5eb8ee76065L, 0x6a543760a80b1f3aL, 0x8f60534dd340ca57L, 0x12baa8284d797838L, 0xcd6ecdfd8c53ef1cL, 0xe5d8ad4c73a92874L, 0x23d2e59d811c864cL, 0xd19d4aeaa703eb7dL, 0x1e87eed70f1788a8L, 0x11125332abfad34cL, 0xa8ac657ba95c916fL, 0xa90c4f262dbc7bb8L, 0x29400cef7adb80efL, 0x14e9af9bb956812fL, 0x6f932c2c939345eL, 0xfb258a8de8eb2ea3L, 0x3d58f7a1921f66eaL, 0xc30268917fbf96c1L, 0x4d40c2709d81100eL, 0x95d5e69f03758c68L, 0x32c941b15595d326L, 0x6fc6301d0c082e58L, 0x3b3d9f2866645445L, 0x61569517fa58fa2aL, 0x8f23c9bfcbdc8646L, 0x8530c0d21f869a7fL, 0x1f64f9ae3c004620L, 0xb86c547d2d25e47cL, 0xe037d2d603a9d77fL, 0x1f6eea464b08b875L, 0xfa9fc96c8d65168fL, 0x7a9ac493a1c5b9e7L, 0xe46bace00bc9731aL, 0x54a2ad90f4ef12acL, 0x48d77cf3a11008d4L, 0xfcbdcae7ec15b569L, 0xe793ed2ced8268f5L, 0xe15e69d07999d34aL, 0x3f383a741313e908L, 0x58bb0749f12078f1L, 0x114936f2ef408b03L, 0xd3a918a5909186abL, 0xcb4ab042da69d296L, 0x413d5849ad0077e4L, 0x3ffe93c8869b1bc7L, 0x769d9ab64415adddL, 0x2047755c542446beL, 0x6aac6fc239b5319bL, 0x725a5c2b6a12ccddL, 0x30f7142a13fb2122L, 0x2a45f9de307e9313L, 0xced2774c14753be3L, 0xae93affa24210d72L, 0x411c592f50044272L, 0xd1290788e30287feL },
            { 0x8ce1c4c706471d7bL, 0xcdd67ef96272a8c7L, 0x417c6a76511cfff6L, 0x8f6ad244643e7f39L, 0xebcc9ed623eaf899L, 0x8ff02150fd67b4b1L, 0x284e01b4fc24a68eL, 0x832a9898a8c5b254L, 0x11211160105f0566L, 0xe1c6800d5d32d01cL, 0x6042e23e42f844cL, 0xca1dd4372e74e133L, 0x52dad2110c88dc7dL, 0x4f5bcd9f7331b183L, 0x1e0a0101756a5af7L, 0xab46fa6f6635488dL, 0xa6dd34d865cd9170L, 0x3060ce59f759057aL, 0x15974a0bcd09a289L, 0xcb93ccb78db7e86fL, 0x9707306684a29d43L, 0x3c97b0f4facd9d25L, 0xdcb0640b45ee9bb1L, 0x6cc5392bc1e31ab8L, 0x4d1372b7336198efL, 0xdb89770e2a04ecbeL, 0xa5a311474cec3252L, 0x8042cb07afec799aL, 0xbdb68580b4e8c7e0L, 0xb14feda54a06da12L, 0xb792680dc2d5b002L, 0x29bbab305059161fL, 0xb6ad02c4e9d27278L, 0xa076359c95aadb04L, 0x28e3cf2f0dc13e8cL, 0x27b730a7845dfea2L, 0x97abd947124d108aL, 0xb731dc3da0ea6409L, 0x969f0a80fe283a6eL, 0x7a77011c435b1552L, 0xca64a10cb1de43dfL, 0x447683b651a2a6abL, 0xbe13575bd7cf555cL, 0x8bccc159436a36b2L, 0xa20fffe1b99aa2b4L, 0x9fa61b934e5fa007L, 0x7653991e21a088bbL, 0xcc555d1ad3cb01cL, 0x27301dd7649c523aL, 0x3dfa949fd0a774eL, 0xe75b95a75ae0d827L, 0xb84bb31c78c10639L, 0x787d29bd21c27115L, 0x3e2ea0b3cc952c47L, 0x4f041dd576be3edL, 0x2c253e71854b0b8aL, 0xd62dedab7d0351deL, 0xd41ccc8b0dfd95ccL, 0x438d1e28cfdf4b92L, 0x13e9be315d3846e3L, 0x210e737e39db85a1L, 0xb668c6f21241b048L, 0x35f0a2f8a5dfe04aL, 0xf3d8da5cda4aa9eaL },
            { 0x9316b953b7a3b660L, 0x89e301e99c1f3c40L, 0x4219bd3c77e2a781L, 0xfbaf2d1a8d33979dL, 0xbb73761047d7518eL, 0xeb35fddf5b8acc7L, 0xe4c85ae96323ad56L, 0x6bdce334e0093ac9L, 0x5432edda55960294L, 0x34895c1823796607L, 0xad307783db4a71bL, 0x41bfec9968a02c93L, 0x5c07d69c1ce9fd4fL, 0xf878c348bed34bbfL, 0xdfe3986725cd65d7L, 0x11c3bcd9c5036af3L, 0xfad44c85379d1890L, 0x3f84fc04fcaf9fc0L, 0x126ce9b5fec818c4L, 0x30889baca9849f34L, 0x1beed68a74a6b89bL, 0x7f1cdf4444ad306fL, 0xf4ebaa6b57735fd1L, 0x8e74856e7d1eb476L, 0x55537ae0467c89a8L, 0x2b0920e28785d747L, 0x14c4390f40a42e20L, 0xf34f9b98fe24be2cL, 0xa732e38393d00a66L, 0x8e8f482134354754L, 0xd74f902fce317c89L, 0x99ce2550ee772e9fL, 0xdb9192b2dd82e673L, 0xeccb382244bb2bb8L, 0x6de942dd57c3cc16L, 0x852a1fdbb3b95d68L, 0xd1e927727bdc20e5L, 0x828148705c929629L, 0xfa10892d12227885L, 0x4d2e5f242d415dfaL, 0xcefdb2072c480ec4L, 0x49eaed755c38bd71L, 0xaee5cb844e8dac9L, 0x87553e37bebc2616L, 0xfd59187d482d218dL, 0x2f3ff1011e5d0bedL, 0xb5d2a51c33cd3b89L, 0x65dcdfc8b3c00454L, 0xa71e8b24c49f81b1L, 0xbafe3aca4c947e0fL, 0x6b7a7478cde395a0L, 0x9ff2feacadc766a1L, 0x5628090f89fd7913L, 0x153228e75aaa50ddL, 0x543153f65886289bL, 0x385a4e5fc34df0bbL, 0xf39ff494c9bead8eL, 0xab417854c0e8006fL, 0x6d1fa03996cddb3eL, 0x422b6b7e5409e2b7L, 0x3488b7d0b8002b6dL, 0xaffabd727eee9674L, 0xd957f5f361c46dffL, 0x55935c4a5b7bcf7cL }}; // Zobrist table for each piece type and position

    public final static long startZobristKey = 2266282855423564102L; // Current Zobrist key, pre calculated for fixed array

    public long currentBoardKey = 0L;
/*    public Zobrist() {
        //initializeZobristTable();
        //startZobristKey = calculateInitialZobristKey();

        //pre calculated now for performance and repeatability

    }*/

/*    private void initializeZobristTable() {
        Random r = new Random();
        for (int pieceType = 0; pieceType < NUM_PIECE_TYPES; pieceType++) {
            for (int position = 0; position < BOARD_SIZE; position++) {
                zobristTable[pieceType][position] = r.nextLong();
            }
        }
    }*/

    private long calculateInitialZobristKeyBeforeBoard() {
        long zobristKey = 0L;
        for (int pieceType = 0; pieceType < 6; pieceType++) {
            for (int position = 0; position < 64; position++) {
                zobristKey ^= zobristTable[pieceType][position];
            }
        }
        return zobristKey;
    }

    public static long applyMove(long currentZobristKey,byte fromIndex, byte toIndex, long r, long b, long rr, long bb, long br, long rb) {
        byte fromType = -1;
        long tempIndexed = (1L << fromIndex); // For performance, save here
        if((r & tempIndexed) != 0) fromType=0;
        if((b & tempIndexed) != 0) fromType=1;
        if((rr & tempIndexed) != 0) fromType=2;
        if((bb & tempIndexed) != 0) fromType=3;
        if((br & tempIndexed) != 0) fromType=4;
        if((rb & tempIndexed) != 0) fromType=5;
        byte toType = -1;
        long tempIndexed2 = (1L << toIndex);
        if((r &  tempIndexed2) != 0) toType=0;
        if((b &  tempIndexed2) != 0) toType=1;
        if((rr & tempIndexed2) != 0) toType=2;
        if((bb & tempIndexed2) != 0) toType=3;
        if((br & tempIndexed2) != 0) toType=4;
        if((rb & tempIndexed2) != 0) toType=5;

/*        if(fromType == 0){
            //Single move
            if(toType == 1){//Enemy single

            }
        }*/
        //TODO: Probably needs to be updated with actual move logic because from toindex isnt enough to
        //model double moves where one remains beneath (would need to be added etc)

/*        // Update Zobrist key for moving piece from 'fromPosition' to 'toPosition'
        currentZobristKey ^= zobristTable[fromType][fromIndex];*/
        if(fromType<2){//All single moves: //TODO: Fixed, was fromIndex <2 before now fromType instead

            currentZobristKey ^= zobristTable[fromType][fromIndex];//Here we can remove the single from the start, no
            //Complex logic like double leaving a piece required.
            boolean isRedSingle = fromType==0;//FIXED, was FROMINDEX
            switch (toType){

                case -1 -> {
                    currentZobristKey ^= zobristTable[fromType][toIndex];//Just put, empty
                }
                case 0 -> {
                    currentZobristKey ^= zobristTable[0][toIndex];
                    currentZobristKey ^= zobristTable[isRedSingle?2:1][toIndex]; // either double or singleblue(capture)
                }
                case 1 -> {
                    currentZobristKey ^= zobristTable[1][toIndex];
                    currentZobristKey ^= zobristTable[isRedSingle?1:3][toIndex];//redsingle or bluedouble
                }
                case 2 -> {// ALL OF THE NEXT ONES ASSUME NO WRONG MOVES AKA NO THIRD RED PIECE CAN GO ONTOP REDDOUBLE
                    currentZobristKey ^= zobristTable[2][toIndex];
                    currentZobristKey ^= zobristTable[5][toIndex];//Add blueonred, remove reddouble
                }
                case 3 -> {
                    currentZobristKey ^= zobristTable[3][toIndex];
                    currentZobristKey ^= zobristTable[4][toIndex];//remove bluedouble, add redonblue
                }
                case 4 -> {
                    currentZobristKey ^= zobristTable[4][toIndex];
                    currentZobristKey ^= zobristTable[3][toIndex];
                }
                case 5 -> {
                    currentZobristKey ^= zobristTable[5][toIndex];
                    currentZobristKey ^= zobristTable[2][toIndex];

                }
            }
            return currentZobristKey;
        }
        //THIS PART IS ONLY FOR DOUBLE PIECES: PREVIOUS WOULD ALREADY HAVE RETURNED OTHERWISE

        // If double piece, different peace remains
/*            if(fromIndex == 2||fromIndex==5){// doublered blueonred, red remains
                currentZobristKey ^= zobristTable[0][fromIndex];
            }else if(fromIndex == 3 || fromIndex == 4){//doubleblue, redonblue, blue remains
                currentZobristKey ^= zobristTable[1][fromIndex];
            }*/
        //int remainingSingle = ((fromType==2||fromType==5)?0:1);
        int remainingSingle = ((fromType==2||fromType==5)?0:1);
        int movingSingle = (fromType==2||fromType==4)?0:1;//Not same as above, same as belongs to team

        currentZobristKey ^= zobristTable[fromType][fromIndex];//REmove original double, it never stay
        // s
        currentZobristKey ^= zobristTable[remainingSingle][fromIndex]; //reddouble,redOnBlue leave red etc and we know

        switch (toType){

            case -1 -> {
                currentZobristKey ^= zobristTable[movingSingle][toIndex];//put single of team at empty pos, remaining was set above
            }
            case 0 -> {
                currentZobristKey ^= zobristTable[0][toIndex];//remove single red
                if(movingSingle==0){
                    currentZobristKey ^= zobristTable[2][toIndex];//put double red
                }else{
                    currentZobristKey ^= zobristTable[1][toIndex];//put single blue
                }
            }
            case 1 -> {
                currentZobristKey ^= zobristTable[1][toIndex];//remove single blue
                if(movingSingle==0){
                    currentZobristKey ^= zobristTable[0][toIndex];//put single red if red jumps on single blue
                }else{
                    currentZobristKey ^= zobristTable[3][toIndex];//put double blue
                }

            }
            case 2 -> { // ALL OF THE NEXT ONES ASSUME NO WRONG MOVES AKA NO THIRD RED PIECE CAN GO ONTOP REDDOUBLE
                //TEAM IS ASSUMED WHEN MOVING ON THEM
                currentZobristKey ^= zobristTable[2][toIndex];//remove double red
                currentZobristKey ^= zobristTable[5][toIndex];//add blue on red
            }
            case 3 -> {
                currentZobristKey ^= zobristTable[3][toIndex];//remove double blue
                currentZobristKey ^= zobristTable[4][toIndex];//add red on blue
            }
            case 4 -> {
                currentZobristKey ^= zobristTable[4][toIndex];//remove redonblue
                currentZobristKey ^= zobristTable[3][toIndex];//add doubleblue (red beat)
            }
            case 5 -> {
                currentZobristKey ^= zobristTable[5][toIndex];//remove blueonred
                currentZobristKey ^= zobristTable[2][toIndex];//add double red (blue beat)
            }
        }
        //currentZobristKey ^= zobristTable[fromType][toIndex];
        return currentZobristKey;
    }

    public void initializeCorrectBoardKey(BitBoard board){
        currentBoardKey = computeInitialZobristKey(board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
    }


    public long computeInitialZobristKey(long r, long b, long rr, long bb, long br, long rb) {
        long initialZobristKey = 0L;

        initialZobristKey ^= computeZobristForBitboard(r, 0);
        initialZobristKey ^= computeZobristForBitboard(b, 1);
        initialZobristKey ^= computeZobristForBitboard(rr, 2);
        initialZobristKey ^= computeZobristForBitboard(bb, 3);
        initialZobristKey ^= computeZobristForBitboard(br, 4);
        initialZobristKey ^= computeZobristForBitboard(rb, 5);

        return initialZobristKey;
    }

    private long computeZobristForBitboard(long bitboard, int pieceType) {
        long zobristKey = 0L;
        while (bitboard != 0) {
            int position = Long.numberOfTrailingZeros(bitboard);
            zobristKey ^= zobristTable[pieceType][position];
            bitboard &= bitboard - 1; // Clear the least significant bit
        }
        return zobristKey;
    }


/*    public long getStartZobristKey() {
        return startZobristKey;
    }*/

    public long getZobristValue(int pieceType, int position) {
        return zobristTable[pieceType][position];
    }

}
