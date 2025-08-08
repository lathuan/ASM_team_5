package com.example.asm_ad;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserChangeListener {
        void onUserChanged();
    }

    private List<UserModel> userList;
    private DatabaseHelper dbHelper;
    private Context context;
    private OnUserChangeListener listener;

    public UserAdapter(List<UserModel> userList, DatabaseHelper dbHelper, Context context, OnUserChangeListener listener) {
        this.userList = userList;
        this.dbHelper = dbHelper;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.tvUsername.setText(user.getUsername());
        holder.tvFullName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());
        holder.tvRole.setText(roleName(user.getRoleId()));

        holder.btnEdit.setOnClickListener(v -> showEditDialog(user));
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa người dùng " + user.getUsername() + " không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        boolean ok = dbHelper.deleteUser(user.getId());
                        if (ok) {
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onUserChanged();
                        } else {
                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private String roleName(int roleId) {
        if (roleId == 2) return "Admin";
        else return "Học sinh";
    }

    private void showEditDialog(UserModel user) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_edit_user, null);

        EditText edtFullName = view.findViewById(R.id.edtDialogFullName);
        EditText edtEmail = view.findViewById(R.id.edtDialogEmail);
        EditText edtPhone = view.findViewById(R.id.edtDialogPhone);
        Spinner spinnerRole = view.findViewById(R.id.spinnerRole);

        edtFullName.setText(user.getFullName());
        edtEmail.setText(user.getEmail());
        edtPhone.setText(user.getPhone());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                new String[]{"Học sinh", "Admin"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        spinnerRole.setSelection(user.getRoleId() == 2 ? 1 : 0);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Sửa thông tin người dùng")
                .setView(view)
                .setPositiveButton("Lưu", null) // override later
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(v -> {
                String fullName = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                int roleId = spinnerRole.getSelectedItemPosition() == 1 ? 2 : 1;

                if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean ok = dbHelper.updateUser(user.getId(), fullName, email, phone, roleId);
                if (ok) {
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onUserChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvFullName, tvEmail, tvPhone, tvRole;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvItemUsername);
            tvFullName = itemView.findViewById(R.id.tvItemFullName);
            tvEmail = itemView.findViewById(R.id.tvItemEmail);
            tvPhone = itemView.findViewById(R.id.tvItemPhone);
            tvRole = itemView.findViewById(R.id.tvItemRole);
            btnEdit = itemView.findViewById(R.id.btnEditUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
