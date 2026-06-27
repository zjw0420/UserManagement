import Modal from './Modal';

interface ConfirmProps {
  open: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
  danger?: boolean;
}

export default function ConfirmDialog({ open, title, message, onConfirm, onCancel, danger }: ConfirmProps) {
  return (
    <Modal open={open} title={title} onClose={onCancel} width={400}>
      <p style={{ marginBottom: 20 }}>{message}</p>
      <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
        <button className="btn btn-cancel" onClick={onCancel}>取消</button>
        <button className={`btn ${danger ? 'btn-danger' : 'btn-primary'}`} onClick={onConfirm}>确定</button>
      </div>
    </Modal>
  );
}
