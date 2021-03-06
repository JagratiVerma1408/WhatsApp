package com.example.whatsapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.ChatActivity;
import com.example.whatsapp.FindFriendsActivity;
import com.example.whatsapp.GroupChatActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.helper.Contacts;
import com.example.whatsapp.helper.MyRecyclerViewAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    View ChatFrag;
    RecyclerView chatsList;
    FloatingActionButton findfriendbutton;
    DatabaseReference chatRef,UsersRef;
    FirebaseAuth auth;
    StorageReference storageReference;
    String currentUserID;
//    RecyclerView list_View;
//    ArrayList<String> list_of_groups=new ArrayList<>();
//    MyRecyclerViewAdapter adapter;
    DatabaseReference GroupRef;
    private String CurrentDate,date,time;

    public ChatFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ChatFrag= inflater.inflate(R.layout.fragment_chat, container, false);
        chatsList=ChatFrag.findViewById(R.id.chats_list);
        findfriendbutton =ChatFrag.findViewById(R.id.btn_create_channel);
        findfriendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), FindFriendsActivity.class);
                startActivity(intent);
            }
        });
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        auth=FirebaseAuth.getInstance();
        currentUserID=auth.getCurrentUser().getUid();
        chatRef= FirebaseDatabase.getInstance().getReference()
                .child("Contacts").child(currentUserID);
        UsersRef=FirebaseDatabase.getInstance().getReference()
                .child("Users");
        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups");
//        Initialize();

//        RetriveandDisplayGroups();

        return ChatFrag;
    }
//    private void RetriveandDisplayGroups() {
//        GroupRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Set<String> set = new HashSet<>();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    set.add((dataSnapshot).getKey());
//                }
//                list_of_groups.clear();
//                list_of_groups.addAll(set);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        adapter = new MyRecyclerViewAdapter(getContext(), list_of_groups);
//        list_View.setAdapter(adapter);
//    }

//    private void Initialize()
//    {
//        list_View =  ChatFrag.findViewById(R.id.group_List);
//        list_View.setLayoutManager(new LinearLayoutManager(getContext()));
//
//    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Contacts model) {
               final String usersID = getRef(position).getKey();
                final String[] retImage = new String[1];
                final String[] retName = new String[1];
                retImage[0]="";
                retName[0]="";
                UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                          if (snapshot.hasChild("image"))
                          {
                               retImage[0] =snapshot.child("image")
                                      .getValue().toString();
                              if (getActivity() == null) {
                                  return;
                              }
                              GetImage(retImage[0], holder.profileImage,getActivity());

                          }

                       retName[0] =snapshot.child("name").getValue().toString();
                       holder.userStatus.setText("Date "+" Time");
                       holder.userName.setText(retName[0]);
                       String retStatus = snapshot.child("status").getValue().toString();
                       holder.contact_status.setVisibility(View.VISIBLE);
                       holder.contact_status.setText(retStatus);

                       if (snapshot.child("userState").hasChild("state"))
                       {
                           String state = snapshot.child("userState").child("state").getValue().toString();
                           date = snapshot.child("userState").child("date").getValue().toString();
                           time = snapshot.child("userState").child("time").getValue().toString();

                           if (state.equals("online"))
                           {
                               holder.userStatus.setVisibility(View.INVISIBLE );
                               holder.onlineIcon.setVisibility(View.VISIBLE);
                               holder.userStatus.setText("online");
                           }
                           else if (state.equals("offline"))
                           {
                               holder.userStatus.setVisibility(View.VISIBLE );
                               holder.onlineIcon.setVisibility(View.INVISIBLE);
                               Calendar calendar = Calendar.getInstance();
                               SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                               CurrentDate = currentDate.format(calendar.getTime());
                               if (!CurrentDate.equals(date)) {
                                   holder.userStatus.setText(date);
                               }else
                               {
                                   holder.userStatus.setText(time.toLowerCase(Locale.ROOT));
                               }
                           }
                       }
                       else
                       {
                           holder.userStatus.setVisibility(View.VISIBLE );
                           holder.onlineIcon.setVisibility(View.INVISIBLE);
                           Calendar calendar = Calendar.getInstance();
                           SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                           CurrentDate = currentDate.format(calendar.getTime());
                           if (!CurrentDate.equals(date)) {
                               holder.userStatus.setText(date);
                           }else
                           {
                               holder.userStatus.setText(time.toLowerCase(Locale.ROOT));
                           }
                           holder.userStatus.setText("offline");
                       }

                       holder.itemView.setOnClickListener(view -> {
                              Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                              chatIntent.putExtra("visit_user_ID",usersID);
                              chatIntent.putExtra("visit_user_name", retName[0]);
                              chatIntent.putExtra("visit_image", retImage[0]);
                              startActivity(chatIntent);
                          });

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view=LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.user_display_layout,parent,false);
               return new ChatsViewHolder(view);
               
            }
        };
        chatsList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView userStatus , userName,contact_status;
        ImageView onlineIcon;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            userName=itemView.findViewById(R.id.users_profile_name);
            userStatus=itemView.findViewById(R.id.users_status);
            contact_status=itemView.findViewById(R.id.contact_status);
            contact_status.setVisibility(View.VISIBLE);
            userStatus.setVisibility(View.VISIBLE );
            onlineIcon=itemView.findViewById(R.id.user_online);

        }
    }

    public void GetImage(String currentUser, CircleImageView imageView, Context context) {
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Profile Images/" + currentUser + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(imageView);

            }
        }).addOnFailureListener(exception -> {

        });
    }
}