package ru.volterr.nam.behaviours.user;

import ru.volterr.nam.agents.UserAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveMsg extends CyclicBehaviour {

	protected UserAgent myUser;
	
	public ReceiveMsg(UserAgent a){
		super(a);
		myUser = a;
	}
	
	@Override
	public void action() {
		ACLMessage msg = myUser.receive();
		if (msg != null) {
		                    // ����� �� ����� ���������� ����� ������ � ����������� ���������
		                    System.out.println(myUser.getLocalName() + " received: " + msg.getContent());
		                }
		// ��������� ���������, ���� � ������� ��������� ������
		// �� �������� ���� �� ���� ���������
		block();
		}

}

