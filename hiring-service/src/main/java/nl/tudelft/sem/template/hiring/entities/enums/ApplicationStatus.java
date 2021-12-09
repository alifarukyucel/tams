package nl.tudelft.sem.template.hiring.entities.enums;

public enum ApplicationStatus {
    PENDING,
    ACCEPTED,
    REJECTED;

    public String statusIs() {
        switch (status)
        {
            case PENDING
                return "pending";
            case ACCEPTED:
                return "accepted";
            case REJECTED:
                return "rejected";
        }
    }

//    public String retrieveStatus(){
//        if(in process) status = ApplicationStatus.PENDING;
//        if()
//    }

}
