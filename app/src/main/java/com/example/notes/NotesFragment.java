package com.example.notes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Field;

import model.Note;

public class NotesFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton fabAdd;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_view_notes);
        fabAdd = view.findViewById(R.id.fab_add_notes_notes_fragment);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = firebaseFirestore
                .collection("Notes")
                .whereEqualTo("userId", firebaseUser.getUid())
                .orderBy("created", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NoteViewHolder holder, final int position, @NonNull final Note note) {
                final CharSequence txt_date = DateFormat.format("EEEE, MMM d, yyyy h:mm:ss a", note.getCreated().toDate());

                holder.noteTitle.setText(note.getTitle());
                holder.noteContent.setText(note.getContent());
                holder.noteDateCreated.setText(txt_date.toString());

                final String txt_userId = firebaseUser.getUid();
//                int code = getRandomColor();
//                holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(code, null));

                final String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ViewNotes.class);
                        intent.putExtra("title", note.getTitle());
                        intent.putExtra("content", note.getContent());
                        intent.putExtra("created", txt_date.toString());
                        intent.putExtra("noteID", docId);
                        intent.putExtra("userId", txt_userId);
                        v.getContext().startActivity(intent);
                    }
                });

                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public boolean onLongClick(View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);

                        Object menuHelper;
                        Class[] argTypes;
                        try {
                            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                            fMenuHelper.setAccessible(true);
                            menuHelper = fMenuHelper.get(popupMenu);
                            argTypes = new Class[]{boolean.class};
                            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                        } catch (Exception e) {

                        }

                        popupMenu.setGravity(Gravity.START);
                        popupMenu.getMenu()
                                .add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(getActivity(), EditNotes.class);
                                intent.putExtra("title", note.getTitle());
                                intent.putExtra("content", note.getContent());
                                intent.putExtra("created", txt_date.toString());
                                intent.putExtra("noteID", docId);
                                intent.putExtra("userId", txt_userId);
                                startActivity(intent);
                                return false;
                            }
                        })
                                .setIcon(R.drawable.ic_edit_black).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

                        popupMenu.getMenu()
                                .add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReference = firebaseFirestore
                                        .collection("Notes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "Note Deleted!!!", Toast.LENGTH_SHORT).show();
                                        startActivity(getActivity().getIntent());
                                        getActivity().overridePendingTransition(0, 0);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Unable to delete note!!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        })
                                .setIcon(R.drawable.ic_delete_black).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

                        popupMenu.show();

                        return true;
                    }
                });

                ImageView optionsMenu = holder.view.findViewById(R.id.img_options_menu);

                optionsMenu.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);

                        Object menuHelper;
                        Class[] argTypes;
                        try {
                            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                            fMenuHelper.setAccessible(true);
                            menuHelper = fMenuHelper.get(popupMenu);
                            argTypes = new Class[]{boolean.class};
                            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                        } catch (Exception e) {

                        } // This try/catch block is to force show the icons on the popup menu

                        popupMenu.setGravity(Gravity.START);
                        popupMenu.getMenu()
                                .add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(getActivity(), EditNotes.class);
                                intent.putExtra("title", note.getTitle());
                                intent.putExtra("content", note.getContent());
                                intent.putExtra("created", txt_date.toString());
                                intent.putExtra("noteID", docId);
                                intent.putExtra("userId", txt_userId);
                                startActivity(intent);
                                return false;
                            }
                        })
                                .setIcon(R.drawable.ic_edit_black).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

                        popupMenu.getMenu()
                                .add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReference = firebaseFirestore
                                        .collection("Notes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "Note Deleted!!!", Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                new NotesFragment()).commit();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Unable to delete note!!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        })
                                .setIcon(R.drawable.ic_delete_black).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }

//            private int getRandomColor() {
//                List<Integer> colorCodes = new ArrayList<>();
//                colorCodes.add(R.color.pink);
//                colorCodes.add(R.color.lightBlue);
//                colorCodes.add(R.color.gray);
//                colorCodes.add(R.color.cyan);
//                colorCodes.add(R.color.green);
//                colorCodes.add(R.color.brown);
//                colorCodes.add(R.color.blue);
//                colorCodes.add(R.color.purple);
//                colorCodes.add(R.color.red);
//
//                Random random = new Random();
//                int randomNumber = random.nextInt(colorCodes.size());
//                return colorCodes.get(randomNumber);
//            }
        };

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddNotes.class));
            }
        });

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(noteAdapter);

        return view;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent, noteDateCreated;
        ImageView noteOptionsMenu;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.txt_title_notes);
            noteContent = itemView.findViewById(R.id.txt_content);
            noteDateCreated = itemView.findViewById(R.id.txt_date_created_notes);
            noteOptionsMenu = itemView.findViewById(R.id.img_options_menu);
            mCardView = itemView.findViewById(R.id.note_card);
            view = itemView;
        }
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
//        MenuItem item = menu.findItem(R.id.menu_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                searchData(s);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                searchData(s);
//                return true;
//            }
//        });
//    }

    @Override
    public void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }
}
