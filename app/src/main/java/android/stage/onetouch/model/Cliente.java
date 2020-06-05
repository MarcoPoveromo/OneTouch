package android.stage.onetouch.model;

import java.util.List;

public class Cliente {
        private String mId;
        private String mNome;
        private List<Sede> mSedi;

        public Cliente(String mId, String mNome, List<Sede> mSedi){
            this.mId = mId;
            this.mNome = mNome;
            this.mSedi = mSedi;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getNome() {
            return mNome;
        }

        public void setNome(String nome) {
            mNome = nome;
        }

        public List<Sede> getSedi() {
            return mSedi;
        }

        public void setSedi(List<Sede> sedi) {
            mSedi = sedi;
        }
    }
