package com.demo.example.Parser;

import android.app.Activity;
import android.content.Context;

import com.demo.example.R;

import java.util.ArrayList;
import java.util.HashMap;


public class Frame_Parser {
    public static final String CORDINATE = "cordinate";
    public static final String IMAGEID = "ImageId";
    public static final String THUMB_URL = "thumburl";
    Context context;
    ArrayList<HashMap<String, String>> datafromDB;
    static int[] frames_thumbnails = {R.drawable.frametree_2t, R.drawable.frametree_6t, R.drawable.frame_1t, R.drawable.frame_2t, R.drawable.frame_3t, R.drawable.frametree_7t, R.drawable.frametree_8t, R.drawable.frame_4t, R.drawable.frame_5t, R.drawable.frame_6t, R.drawable.frametree_9t, R.drawable.frametree_10t, R.drawable.frame_7t, R.drawable.frame_8t, R.drawable.frame_9t, R.drawable.frametree_11t, R.drawable.frametree_1t, R.drawable.frame_10t, R.drawable.frame_11t, R.drawable.frame_12t, R.drawable.frametree_3t, R.drawable.frametree_4t, R.drawable.frame_13t, R.drawable.frame_14t, R.drawable.frame_15t, R.drawable.frametree_5t, R.drawable.frametree_12t, R.drawable.frame_16t, R.drawable.frame_17t, R.drawable.frame_18t, R.drawable.frametree_13t, R.drawable.frametree_14t, R.drawable.frame_19t, R.drawable.frame_20t, R.drawable.frame_21t, R.drawable.frametree_15t, R.drawable.frametree_16t, R.drawable.frame_22t, R.drawable.frame_23t, R.drawable.frame_24t, R.drawable.frametree_17t, R.drawable.frametree_18t, R.drawable.frame_25t, R.drawable.frame_26t, R.drawable.frame_27t, R.drawable.frame_28t, R.drawable.frame_29t};
    static int[] frames = {R.drawable.frametree_2, R.drawable.frametree_6, R.drawable.frame1, R.drawable.frame2, R.drawable.frame3, R.drawable.frametree_7, R.drawable.frametree_8, R.drawable.frame4, R.drawable.frame5, R.drawable.frame6, R.drawable.frametree_9, R.drawable.frametree_10, R.drawable.frame7, R.drawable.frame8, R.drawable.frame9, R.drawable.frametree_11, R.drawable.frametree_1, R.drawable.frame10, R.drawable.frame11, R.drawable.frame12, R.drawable.frametree_3, R.drawable.frametree_4, R.drawable.frame13, R.drawable.frame14, R.drawable.frame15, R.drawable.frametree_5, R.drawable.frametree_12, R.drawable.frame16, R.drawable.frame17, R.drawable.frame18, R.drawable.frametree_13, R.drawable.frametree_14, R.drawable.frame19, R.drawable.frame20, R.drawable.frame21, R.drawable.frametree_15, R.drawable.frametree_16, R.drawable.frame22, R.drawable.frame23, R.drawable.frame24, R.drawable.frametree_17, R.drawable.frametree_18, R.drawable.frame25, R.drawable.frame26, R.drawable.frame27, R.drawable.frame28, R.drawable.frame29};
    public static String[] frames_Cordinates = {"262:67:433:279, 512:118:705:350, 47:287:240:520, 437:447:633:682", "192:181:316:305, 418:135:558:292, 144:402:289:554, 346:443:460:577, 490:360:663:486", "30:78:390:398,405:328:688:621,61:444:310:691", "8:37:306:301,443:37:698:298,365:337:593:545", "81:170:326:368,420:137:667:284,243:387:579:572", "155:30:319:178, 409:65:629:219, 54:289:175:421, 226:265:386:379, 517:312:670:442", "188:85:340:217, 427:127:578:263, 153:304:325:444, 526:319:672:452", "261:35:536:209,73:220:237:479,263:316:490:504,516:219:712:434", "275:45:519:214,120:240:376:396,397:241:677:394,187:419:618:572", "260:380:634:631,67:89:328:464", "141:72:309:205, 391:30:591:199, 193:250:418:429, 531:265:727:444, 268:474:466:630", "266:292:466:504, 197:522:348:686, 391:533:546:678", "76:194:293:405,385:78:604:280:,82:481:293:690,544:533:757:737", "310:127:468:311,122:326:312:474,465:334:657:484", "117:515:265:634,566:494:703:606,735:435:889:560", "201:63:419:182, 316:219:464:362, 534:157:673:356, 119:293:232:466, 460:427:589:561", "322:100:453:228, 255:257:369:374, 420:257:539:374, 211:424:328:540, 476:424:592:538, 170:582:287:699, 521:582:641:697", "61:322:315:694,381:242:617:597,629:401:877:791", "359:30:635:236,30:110:282:293,18:328:246:499,400:442:733:733", "110:36:316:312,338:271:619:555,583:572:790:850", "305:10:466:193, 83:182:202:316, 350:315:470:446, 583:180:704:316, 159:440:263:560, 535:440:643:555", "352:66:484:244, 45:176:252:365, 544:167:701:353, 65:479:217:650, 329:424:522:571, 600:428:735:624", "328:82:576:333,106:310:315:549,340:376:557:596,588:308:780:539", "257:132:439:273,76:373:327:565,386:371:637:572", "292:113:536:290,165:280:399:451,420:293:665:465,73:459:313:637,330:458:488:701,508:477:730:632", "55:248:157:364, 275:301:367:414, 519:193:611:305, 310:472:390:567, 493:506:585:623, 617:382:710:498", "230:40:483:201, 196:228:471:384, 498:181:676:377, 140:401:292:591, 365:406:613:649", "34:84:267:324,517:59:714:251,271:262:408:409,71:491:333:756,466:631:730:891", "26:14:298:280,24:295:482:733", "55:122:173:289,266:145:439:262,538:120:756:385,70:392:245:502,343:406:464:573,569:482:743:591,370:718:540:841,636:693:754:868", "358:99:483:344, 168:186:336:375, 507:179:632:352, 211:392:336:509, 392:363:589:517", "321:47:438:185, 118:118:280:319, 454:111:665:352, 235:332:445:494", "79:163:274:345,291:25:461:200,500:165:680:358,59:415:222:576,530:395:677:559,228:548:348:671,397:550:529:677", "470:564:681:780,103:441:324:663,350:335:483:471,594:381:729:517,230:778:419:964,565:820:739:1007", "23:323:231:560,299:323:500:525,563:331:784:601", "261:113:440:270, 331:284:613:465, 37:342:165:472, 177:328:314:619, 637:362:769:550, 474:492:628:621", "145:173:310:394, 330:141:478:395, 498:161:626:372, 278:411:548:585", "10:132:245:356,361:80:546:240,166:357:375:549,378:453:659:684", "106:24:313:233,459:127:763:430,282:391:498:611,524:524:783:781", "194:62:355:221,352:9:515:173,514:97:657:247,47:220:194:376,195:231:320:364,383:213:534:365,554:270:729:445,253:349:471:559", "270:4:490:151, 234:181:555:314, 151:333:421:528, 438:333:618:548", "150:1:345:240, 589:2:777:230, 133:289:296:486, 513:260:708:470, 330:488:525:725", "217:94:485:343,26:356:212:538,241:474:491:719,560:809:745:996", "393:60:549:212,536:196:640:305,211:225:400:416,73:405:227:558,574:405:693:529,523:546:621:646", "42:17:284:265,477:34:687:242,490:637:745:893", "308:147:470:253,274:355:510:694,106:642:226:824,558:647:686:826", "348:244:508:345,76:326:238:428,625:393:786:507,82:499:241:601,528:559:692:670,119:713:201:809"};

    public ArrayList<Frame_Photo> getFRAMEThumbnails(Activity activity) {
        ArrayList<Frame_Photo> arrayList = new ArrayList<>();
        ArrayList<HashMap<String, String>> arrayList2 = new ArrayList<>();
        this.datafromDB = arrayList2;
        if (arrayList2.size() > 0 || this.datafromDB != null) {
            for (int i = 0; i < this.datafromDB.size(); i++) {
                Frame_Photo frame_Photo = new Frame_Photo();
                frame_Photo.setImageId(this.datafromDB.get(i).get(IMAGEID));
                frame_Photo.setThumnailIdURL(this.datafromDB.get(i).get(THUMB_URL));
                frame_Photo.Coordinate = this.datafromDB.get(i).get(CORDINATE);
                arrayList.add(frame_Photo);
            }
        }
        for (int i2 = 0; i2 < frames.length; i2++) {
            Frame_Photo frame_Photo2 = new Frame_Photo();
            frame_Photo2.ThumnailId = frames_thumbnails[i2];
            frame_Photo2.FullImage = frames[i2];
            frame_Photo2.Coordinate = frames_Cordinates[i2];
            arrayList.add(frame_Photo2);
        }
        return arrayList;
    }
}
