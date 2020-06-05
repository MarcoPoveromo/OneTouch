package android.stage.onetouch.model;

public class Sede {
        private String mId;
        private String mIndirizzo;

        public Sede(String mId, String mIndirizzo){
            this.mId = mId;
            this.mIndirizzo = mIndirizzo;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getIndirizzo() {
            return mIndirizzo;
        }

        public void setIndirizzo(String indirizzo) {
            mIndirizzo = indirizzo;
        }
}
