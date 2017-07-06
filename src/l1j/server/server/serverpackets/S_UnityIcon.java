package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_UnityIcon extends ServerBasePacket {

	public S_UnityIcon(int DECREASE, int DECAY_POTION, int SILENCE, int VENOM_RESIST, int WEAKNESS, int DISEASE,
			int DRESS_EVASION, int BERSERKERS, int NATURES_TOUCH, int WIND_SHACKLE, 
			int ERASE_MAGIC, int ADDITIONAL_FIRE, int ELEMENTAL_FALL_DOWN, int ELEMENTAL_FIRE,
			int STRIKER_GALE, int SOUL_OF_FLAME, int POLLUTE_WATER,
			int EXP_POTION, int SCROLL, int SCROLLTPYE,
			int CONCENTRATION, int INSIGHT, int PANIC,
			int MORTAL_BODY, int HORROR_OF_DEATH, int FEAR,
			int PATIENCE, int GUARD_BREAK, int DRAGON_SKIN, int STATUS_FRUIT,
			int COMA, int COMA_TYPE, int CRAY_TIME, int CRAY, int MAAN_TIME, int MAAN, int FEATHER_BUFF, int FEATHER_TYPE) {
		writeC(Opcodes.S_EVENT);
		writeC(0x14);
		writeC(0x74);
		writeC(0x00);
		writeC(0x00);
		writeD(0);
		writeC(DECREASE); // 디크리즈 웨이트 DECREASE
		writeC(DECAY_POTION); // 디케이 포션
		writeC(0x00);
		writeC(SILENCE); // 사일런스
		writeC(VENOM_RESIST); // 베놈 레지스트 10
		writeC(WEAKNESS); // 위크니스
		writeC(DISEASE); // 디지즈
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(DRESS_EVASION);                      //드레스이베이전 !
		// 20
		writeC(BERSERKERS);                        //버서커스 !
		writeC(NATURES_TOUCH);                       //네이쳐스터치
		writeC(WIND_SHACKLE);                         //윈드셰클 10
		writeC(ERASE_MAGIC);                         //이레이즈매직
		writeC(0x00);                              //디지즈아이콘인데 설명은 카운터미러효과라고 되있음
		writeC(ADDITIONAL_FIRE);                               //어디셔널 파이어
		writeC(ELEMENTAL_FALL_DOWN);                //엘리맨탈폴다운   
		writeC(0x00);
		writeC(ELEMENTAL_FIRE);                     //엘리맨탈 파이어
		writeC(0x00);// 30
		writeC(0x00);              //기척을지워 괴물들이 눈치채지못하게합니다???아이콘도이상함
		writeC(0x00);
		writeC(STRIKER_GALE);                        // 스트라이커게일
		writeC(SOUL_OF_FLAME);                     //소울오브 프레임
		writeC(POLLUTE_WATER);                          //플루투워터 
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);                //속성저항력 10? 
		writeC(0x00);// 40
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);             //sp
		writeC(EXP_POTION);            //exp
		writeC(SCROLL);      //전투강화주문서 123 다있음?
		writeC(SCROLLTPYE);             //0-hp50hpr4, 1-mp40mpr4, 2-추타3공성3sp3  20 50
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(CONCENTRATION);                         //컨센트레이션
		writeC(INSIGHT);                        //인사이트
		writeC(PANIC);                       //패닉
		writeC(MORTAL_BODY);                       //모탈바디                 
		writeC(HORROR_OF_DEATH);                       //호어오브데스
		writeC(FEAR);                     //피어 60
		writeC(PATIENCE);                      //페이션스
		writeC(GUARD_BREAK);                      //가드브레이크
		writeC(DRAGON_SKIN);                   //드래곤스킨
		writeC(STATUS_FRUIT);             //유그드라  30
		writeC(0x14);
		writeC(0x00);
		writeC(COMA);//시간
		writeC(COMA_TYPE);//타입
		writeC(0x00);
		writeC(0x00);//70
		writeC(0x1a);
		writeC(0x35);
		writeC(0x0d);
		writeC(0x00);
		writeC(0xf4);
		writeC(0xa5);
		writeC(0xdc);
		writeC(0x4a);
		writeC(CRAY_TIME);	// (int)(codetest+0.5) / 32
		writeC(CRAY);	// 45크레이축복, 60무녀사엘축복80
		writeC(MAAN_TIME);	//(int)(codetest+0.5) / 32
		writeC(MAAN);	// 46지룡, 47수룡, 48풍룡, 49화룡, 50지룡,수룡 51지룡,수룡,풍룡 52지룡,수룡,풍룡,화룡
		writeC(0xa1);
		writeC(0x09);
		writeC(0x35);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);//90
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(FEATHER_BUFF);	//(int)(codetest+0.5) / 16
		writeC(FEATHER_TYPE);	// 70= 전부 71공성,주술력,최대HP/MP 뎀지감소 증가, 72최대HP,MP증가 AC향상, 73AC향상
		writeC(0x00);
		writeC(0x00);
//		writeC(0x04);
//		writeC(0x16);		
		// 100
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		// 110
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
	}
	
/*
	public S_BuffIcon() {
	  writeC(Opcodes.S_SKILLICONGFX);
	  writeC(0x14);
	  writeC(0); // 메디테이션
	  writeH(0); // 없음
	  writeC(0); // 디크리즈 웨이트
	  writeC(0); // 디케이 포션
	  writeC(0); // 앱솔루트 배리어
	  writeC(0); // 사일런스
	  writeC(0); // 베놈 레지스트
	  writeC(0); // 위크니스
	  writeC(0); // 디지즈
	  writeD(0); // 없음                16
	  
	  writeH(0); // 없음
	  writeC(0); // 없음
	  writeC(0); // 드레스 이베이젼
	  writeC(0); // 버서커스 
	  writeC(0); // 네이쳐스 터치
	  writeC(0); // 윈드셰클
	  writeC(0); // 이레이즈 매직
	  writeC(0); // 디지즈 (카운터 미러 효과)
	  writeC(0); // 엑조틱 바이탈라이즈
	  writeC(0); // 엘리멘탈 폴다운
	  writeC(0);
	  writeC(0); // 어디셔널 파이어
	  writeC(0);
	  writeC(0); // (기척을 지워 인식하지 못하게 합니다)
	  writeC(0);          
	  
	  writeC(0); // 스트라이커 게일
	  writeC(0); // 소울 오브 프레임
	  writeC(0); // 폴루트 워터
	  writeH(0);
	  writeC(0);
	  writeC(0); // 일반 요리 (속성저항력 10의 증가 효과가 있습니다)
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0); // 지혜의 물약
	  writeC(0); // 경험치 물약            
	  
	  writeC(0);
	  writeC(0); // 컬러풀 주문서
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0); // 컨센트 레이션
	  writeC(0); // 인사이트
	  writeC(0); // 패닉
	  writeC(0); // 모탈바디
	  writeC(0); // 호러 오브 데스
	  writeC(0); // 피어
	  writeC(0); // 페이션스
	  writeC(0); // 가드 브레이크
	  writeC(0); // 드래곤 스킨 
	  writeC(0);                      
	  
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);                          
	 }
	 */

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
